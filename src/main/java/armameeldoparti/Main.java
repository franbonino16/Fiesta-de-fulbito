package armameeldoparti;

import armameeldoparti.controllers.AnchoragesController;
import armameeldoparti.controllers.HelpController;
import armameeldoparti.controllers.MainMenuController;
import armameeldoparti.controllers.NamesInputController;
import armameeldoparti.controllers.ResultsController;
import armameeldoparti.controllers.SkillPointsInputController;
import armameeldoparti.models.Error;
import armameeldoparti.models.Player;
import armameeldoparti.models.Position;
import armameeldoparti.models.ProgramView;
import armameeldoparti.utils.common.CommonFields;
import armameeldoparti.utils.common.CommonFunctions;
import armameeldoparti.utils.common.Constants;
import armameeldoparti.views.AnchoragesView;
import armameeldoparti.views.HelpView;
import armameeldoparti.views.MainMenuView;
import armameeldoparti.views.NamesInputView;
import armameeldoparti.views.ResultsView;
import armameeldoparti.views.SkillPointsInputView;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.IntStream;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Main class, only for program start-up.
 *
 * @author Bonino, Francisco Ignacio.
 *
 * @version 3.0.0
 *
 * @since 1.0
 */
public final class Main {

  // --------------------------------------------------------------- Constructor ---------------------------------------------------------------------

  /**
   * Empty, private constructor.
   */
  private Main() {
    // Body not needed
  }

  // --------------------------------------------------------------- Main entry point ----------------------------------------------------------------

  /**
   * Starts the program by initializing the fields needed along with the program's graphical properties, and making the main menu
   * view visible.
   *
   * @param args Program arguments (not used).
   */
  public static void main(String[] args) {
    // Establishes the main monitor as the active monitor by default
    CommonFields.setActiveMonitor(GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                     .getDefaultScreenDevice());
    CommonFields.setAnchoragesEnabled(false);
    CommonFields.setControllerMap(new EnumMap<>(ProgramView.class));
    CommonFields.setPlayersAmountMap(new EnumMap<>(Position.class));
    CommonFields.setPlayersSets(new TreeMap<>());
    CommonFields.setPositionsMap(Map.of(Position.CENTRAL_DEFENDER, Constants.POSITION_CENTRAL_DEFENDERS,
                                        Position.LATERAL_DEFENDER, Constants.POSITION_LATERAL_DEFENDERS,
                                        Position.MIDFIELDER, Constants.POSITION_MIDFIELDERS,
                                        Position.FORWARD, Constants.POSITION_FORWARDS,
                                        Position.GOALKEEPER, Constants.POSITION_GOALKEEPERS));

    setUpGeneralGraphicalProperties();
    setPlayersDistribution();
    populatePlayersSets();
    setUpControllers();

    SwingUtilities.invokeLater(((MainMenuController) CommonFunctions.getController(ProgramView.MAIN_MENU))::showView);
  }

  // ---------------------------------------------------------------- Private methods ----------------------------------------------------------------

  /**
   * Populates the players sets with empty players.
   */
  private static void populatePlayersSets() {
    Arrays.stream(Position.values())
          .forEach(position -> CommonFields.getPlayersSets()
                                           .put(position, IntStream.range(0, CommonFields.getPlayersAmountMap()
                                                                                         .get(position) * 2)
                                                                   .mapToObj(i -> new Player("", position))
                                                                   .toList()));
  }

  /**
   * Gets the number of players for each position per team using regular expressions.
   *
   * <p>{@code [CLMFG].+>.+}: Retrieves the lines that start with C, L, M, F, or G, followed by at least one '>' character (these are the lines that
   * matters in the .pda file).
   *
   * <p>{@code (?!(?<=X)\\d).}: Gets the part of the line that is not a number that we are interested in (the number would take the place of the X).
   *
   * <p>If the .pda file is modified in terms of the order of the important lines, it must be taken into account that {@code Position.values()[index]}
   * trusts that what is found corresponds to the order in which the values in the Position enum are declared. Idem, if the order of the Position enum
   * values are changed, it should be noted that {@code Position.values()[index]} trusts the order in which the data will be retrieved from the .pda
   * file and, therefore, you should review the order of the important lines in the file.
   */
  private static void setPlayersDistribution() {
    try (BufferedReader buff = new BufferedReader(
           new InputStreamReader(
             Objects.requireNonNull(CommonFunctions.class
                                                   .getClassLoader()
                                                   .getResourceAsStream(Constants.PATH_DOCS + Constants.FILENAME_PDA))
           )
         )
    ) {
      List<String> filteredLines = buff.lines()
                                       .filter(line -> line.matches(Constants.REGEX_PDA_DATA_RETRIEVE))
                                       .toList();

      IntStream.range(0, filteredLines.size())
               .forEach(index -> CommonFields.getPlayersAmountMap()
                                             .put(Position.values()[index],
                                                  Integer.parseInt(filteredLines.get(index)
                                                                                .replaceAll(Constants.REGEX_PLAYERS_AMOUNT, ""))));
    } catch (IOException e) {
      CommonFunctions.exitProgram(Error.ERROR_FILES);
    }
  }

  /**
   * Creates the controllers and assigns their corresponding view to control.
   */
  private static void setUpControllers() {
    CommonFields.getControllerMap()
                .putAll(Map.of(ProgramView.MAIN_MENU, new MainMenuController(new MainMenuView()),
                               ProgramView.HELP, new HelpController(new HelpView()),
                               ProgramView.NAMES_INPUT, new NamesInputController(new NamesInputView()),
                               ProgramView.ANCHORAGES, new AnchoragesController(new AnchoragesView()),
                               ProgramView.SKILL_POINTS, new SkillPointsInputController(new SkillPointsInputView()),
                               ProgramView.RESULTS, new ResultsController(new ResultsView())));
  }

  /**
   * Sets up the program's GUI properties.
   */
  private static void setUpGeneralGraphicalProperties() {
    UIManager.put("Button.background", Constants.COLOR_GREEN_DARK);
    UIManager.put("Button.foreground", Color.WHITE);
    UIManager.put("CheckBox.background", Constants.COLOR_GREEN_LIGHT);
    UIManager.put("CheckBox.focus", Constants.COLOR_GREEN_LIGHT);
    UIManager.put("ComboBox.background", Constants.COLOR_GREEN_MEDIUM);
    UIManager.put("ComboBox.foreground", Color.WHITE);
    UIManager.put("ComboBox.selectionBackground", Constants.COLOR_GREEN_MEDIUM);
    UIManager.put("ComboBox.selectionForeground", Color.WHITE);
    UIManager.put("Label.background", Constants.COLOR_GREEN_MEDIUM_LIGHT);
    UIManager.put("OptionPane.background", Constants.COLOR_GREEN_LIGHT);
    UIManager.put("Panel.background", Constants.COLOR_GREEN_LIGHT);
    UIManager.put("RadioButton.background", Constants.COLOR_GREEN_LIGHT);
    UIManager.put("RadioButton.focus", Constants.COLOR_GREEN_LIGHT);
    UIManager.put("Separator.background", Constants.COLOR_GREEN_LIGHT);
    UIManager.put("Separator.foreground", Constants.COLOR_GREEN_MEDIUM);
    UIManager.put("Spinner.background", Constants.COLOR_GREEN_LIGHT_WHITE);
    UIManager.put("FormattedTextField.background", Constants.COLOR_GREEN_LIGHT_WHITE);
    UIManager.put("TextArea.background", Constants.COLOR_GREEN_LIGHT_WHITE);
    UIManager.put("TextField.selectionBackground", Constants.COLOR_GREEN_DARK_MEDIUM);
    UIManager.put("TextField.selectionForeground", Constants.COLOR_GREEN_LIGHT_WHITE);

    try {
      Font programFont = Font.createFont(
        Font.TRUETYPE_FONT,
        Objects.requireNonNull(CommonFunctions.class
                                              .getClassLoader()
                                              .getResourceAsStream(Constants.PATH_TTF + Constants.FILENAME_FONT),
                               Constants.MSG_ERROR_NULL_RESOURCE)
      ).deriveFont(Constants.FONT_SIZE);

      GraphicsEnvironment.getLocalGraphicsEnvironment()
                         .registerFont(programFont);

      setProgramFont(programFont);
    } catch (IOException | FontFormatException e) {
      CommonFunctions.exitProgram(Error.ERROR_GUI);
    }
  }

  /**
   * Sets the program font.
   *
   * @param font Font to use.
   */
  private static void setProgramFont(Font font) {
    UIManager.getDefaults()
             .keySet()
             .stream()
             .filter(k -> UIManager.get(k) instanceof FontUIResource)
             .forEach(k -> UIManager.put(k, font));
  }
}