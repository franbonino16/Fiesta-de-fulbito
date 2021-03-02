/**
 * @author Bonino, Francisco Ignacio.
 * 
 * @version 1.0.0
 * 
 * @since 28/02/2021
 */

import java.util.ArrayList;
import java.util.EnumMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class MixFrame extends JFrame implements ActionListener {

    // Constantes privadas.
    private static final int frameWidth = 450; // Ancho de la ventana.
    private static final int frameHeight = 344; // Alto de la ventana.
    private static final String[] options = { "Agregar defensores centrales", "Agregar defensores laterales",        // Opciones para el menú desplegable.
                                              "Agregar mediocampistas", "Agregar delanteros", "Agregar comodines" };

    // Campos privados.
    private ImageIcon icon; // Icono para la ventana.
    private JPanel panel; // Panel para la ventana de mezcla.
    private JComboBox<String> comboBox; // Menú desplegable.
    private ArrayList<Player> setCD, setLD, setMF, setFW, setWC;
    private ArrayList<JTextField> textFieldCD, textFieldLD, textFieldMF, textFieldFW, textFieldWC; // Arreglos de campos de texto para ingresar nombres.
    private EnumMap<Position, Integer> playersAmountMap; // Mapa que asocia a cada posición un valor numérico (cuántos jugadores por posición por equipo).

    /**
     * Constructor. Aquí se crea la ventana de mezcla.
     * 
     * @throws  IOException Cuando hay un error de lectura en los archivos PDA.
     */
    public MixFrame(int playersAmount, ImageIcon icon) throws IOException {
        this.icon = icon;

        playersAmountMap = new EnumMap<>(Position.class);

        textFieldCD = new ArrayList<>();
        textFieldLD = new ArrayList<>();
        textFieldMF = new ArrayList<>();
        textFieldFW = new ArrayList<>();
        textFieldWC = new ArrayList<>();

        setCD = new ArrayList<>();
        setLD = new ArrayList<>();
        setMF = new ArrayList<>();
        setFW = new ArrayList<>();
        setWC = new ArrayList<>();

        collectPDData(playersAmount);

        playersAmountMap.forEach((key, value) -> System.out.println("POSICIÓN " + key + ": " + value));

        initializeComponents("Ingreso de jugadores - Fútbol " + playersAmount);

        setVisible(true);
    }

    // ----------------------------------------Métodos privados---------------------------------

    /**
     * Este método rescata la cantidad de jugadores para cada posición por equipo
     * mediante expresiones regulares.
     * 
     * [CLMFW].>+.[0-9] : Matchea las líneas que comiencen con C, L, M, F, ó W,
     * estén seguidas por al menos un caracter >, y luego tengan algún número.
     * 
     * [A-Z].>+. : Matchea el trozo de la línea que no es un número.
     * 
     *                          ¡¡¡IMPORTANTE!!!
     * 
     * Si los archivos .PDA son modificados en cuanto a orden de las líneas
     * importantes (C >> NÚMERO, etc.), se debe tener en cuenta que
     * Position.values()[index] confía en que lo hallado se corresponde con
     * el orden en el que están declarados los valores en el enum Position.
     * Idem, si se cambian de orden los valores del enum Position, se deberá
     * tener en cuenta que Position.values()[index] confía en el orden en el
     * que se leerán los datos de los archivos .PDA y, por consiguiente, se
     * deberá rever el orden de las líneas importantes de dichos archivos.
     * 
     * @param   fileName    Nombre del archivo a buscar.
     * 
     * @throws  IOException Si el archivo no existe.
     */
    private void collectPDData(int fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("useful/FDF_F" + fileName + ".PDA"))) {
            String line;
            int index = 0;

            while ((line = br.readLine()) != null)
                if (line.matches("[CLMFW].>+.[0-9]")) {
                    playersAmountMap.put(Position.values()[index], Integer.parseInt(line.replaceAll("[A-Z].>+.", "")));
                    index++;
                }
        }
    }

    /**
     * Este método se encarga de inicializar los componentes
     * de la ventana de mezcla.
     * 
     * @param frameTitle Título de la ventana.
     */
    private void initializeComponents(String frameTitle) {
        setSize(frameWidth, frameHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(frameTitle);
        setResizable(false);
        setIconImage(icon.getImage());

        panel = new JPanel();
        panel.setBounds(0, 0, frameWidth, frameHeight);
        panel.setLayout(null);
        
        addTextFields(Position.CENTRALDEFENDER, textFieldCD, setCD);
        addTextFields(Position.LATERALDEFENDER, textFieldLD, setLD);
        addTextFields(Position.MIDFIELDER, textFieldMF, setMF);
        addTextFields(Position.FORWARD, textFieldFW, setFW);
        addTextFields(Position.WILDCARD, textFieldWC, setWC);

        addComboBox();

        add(panel);
    }

    /**
     * Este método se encarga de agregar la lista desplegable
     * al frame, y setear el handler de eventos a la misma.
     */
    private void addComboBox() {
        comboBox = new JComboBox<>(options);

        comboBox.setBounds(5, 5, 200, 30);
        comboBox.addActionListener(this);

        updateOutput(comboBox.getSelectedItem().toString()); // Para que se muestre el output correspondiente
                                                             // al estado inicial del JComboBox.

        panel.add(comboBox);
    }

    /**
     * Este método se encarga de crear, almacenar y configurar
     * los campos de texto correspondientes a cada posición.
     * 
     * @param   position        Posición a buscar en el EnumMap.
     * @param   textFieldSet    Arreglo de campos de texto para cada posición.
     */
    private void addTextFields(Position position, ArrayList<JTextField> textFieldSet, ArrayList<Player> playersSet) {
        for (int i = 0; i < (playersAmountMap.get(position) * 2); i++) {
            JTextField aux = new JTextField(position + " #" + (i + 1));

            aux.setBounds(5, (45 * (i + 1)), 201, 30);

            aux.addActionListener(new ActionListener() {
                /**
                 * En este método se evalúa que el string ingresado
                 * como nombre de jugador sea válido. 
                 * 
                 * @param   e   Evento ocurrido (nombre ingresado).
                 */
                public void actionPerformed(ActionEvent e) {
                    String name = aux.getText().trim().toUpperCase().replaceAll(" ", "_");

                    if (/*name.length() == 0 ||*/ name.length() > 12 || isEmptyString(name))
                        JOptionPane.showMessageDialog(null, "El nombre del jugador no puede estar vacío o tener más de 12 caracteres",
                                                      "¡Error!", JOptionPane.ERROR_MESSAGE, null);
                    else if (alreadyExists(playersSet, name))
                        JOptionPane.showMessageDialog(null, "¡Ya hay un jugador con ese nombre!",
                                                        "¡Error!", JOptionPane.ERROR_MESSAGE, null);
                    else playersSet.add(new Player(name, position));
                }
            });

            textFieldSet.add(aux);
        }

        for (JTextField textField : textFieldSet)
            panel.add(textField);
    }

    /**
     * Indica si una cadena está vacía o no.
     * Si la cadena está compuesta por caracteres
     * en blanco (espacios), se la tomará como vacía.
     * 
     * @param   string    Cadena a analizar
     * 
     * @return  Si la cadena está vacía o no.
     */
    private boolean isEmptyString(String string) {
    	char[] charArray = string.toCharArray();
    	
    	for (int i = 0; i < charArray.length; i++)
    		if (charArray[i] != ' ') return false;
    	
    	return true;
    }

    /**
     * @param list
     * @param name
     * @return
     */
    private boolean alreadyExists(ArrayList<Player> playersSet, String name)
    {
    	for(Player player : playersSet)
			if(name.equals(player.getName()))
				return true;
    	
    	return false;
    }

    /**
     * Handler para los eventos ocurridos de la lista desplegable.
     * Se trata la fuente del evento ocurrido como un JComboBox y
     * se trata como un String el item seleccionado en el mismo
     * para pasarlo al método updateOutput.
     * 
     * @param   e   Evento ocurrido (item seleccionado).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        updateOutput((String)((JComboBox<?>)e.getSource()).getSelectedItem());
    }

    /**
     * Este método se encarga de actualizar lo mostrado en la
     * ventana en base al item seleccionado en la lista desplegable.
     * 
     * @param   text    Opción seleccionada del arreglo de Strings 'options'.
     */
    private void updateOutput(String text) {
        switch (text) {
            case "Agregar defensores centrales": {
                textFieldCD.forEach((tf) -> tf.setVisible(true));
                textFieldLD.forEach((tf) -> tf.setVisible(false));
                textFieldMF.forEach((tf) -> tf.setVisible(false));
                textFieldFW.forEach((tf) -> tf.setVisible(false));
                textFieldWC.forEach((tf) -> tf.setVisible(false));
                
                break;
            }

            case "Agregar defensores laterales": {
                textFieldCD.forEach((tf) -> tf.setVisible(false));
                textFieldLD.forEach((tf) -> tf.setVisible(true));
                textFieldMF.forEach((tf) -> tf.setVisible(false));
                textFieldFW.forEach((tf) -> tf.setVisible(false));
                textFieldWC.forEach((tf) -> tf.setVisible(false));

                break;
            }

            case "Agregar mediocampistas": {
                textFieldCD.forEach((tf) -> tf.setVisible(false));
                textFieldLD.forEach((tf) -> tf.setVisible(false));
                textFieldMF.forEach((tf) -> tf.setVisible(true));
                textFieldFW.forEach((tf) -> tf.setVisible(false));
                textFieldWC.forEach((tf) -> tf.setVisible(false));

                break;
            }

            case "Agregar delanteros": {
                textFieldCD.forEach((tf) -> tf.setVisible(false));
                textFieldLD.forEach((tf) -> tf.setVisible(false));
                textFieldMF.forEach((tf) -> tf.setVisible(false));
                textFieldFW.forEach((tf) -> tf.setVisible(true));
                textFieldWC.forEach((tf) -> tf.setVisible(false));
                
                break;
            }

            default: {
                textFieldCD.forEach((tf) -> tf.setVisible(false));
                textFieldLD.forEach((tf) -> tf.setVisible(false));
                textFieldMF.forEach((tf) -> tf.setVisible(false));
                textFieldFW.forEach((tf) -> tf.setVisible(false));
                textFieldWC.forEach((tf) -> tf.setVisible(true));
                
                break;
            }
        }
    }
}