import LenguajeAutomatas2.ArbolExpresionGrafico;
import interactivoArbolExpresion.ArbolExpresionInteractivo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuInterface extends JFrame {
    private JSplitPane splitPane;
    private JPanel leftPanel;
    private JPanel mainPanel;

    public MenuInterface() {
        setTitle("Interfaz de Menú");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel izquierdo (Sidebar)
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JButton btnExpresionAArbol = new JButton("Expresión a Árbol");
        JButton btnArbolAExpresion = new JButton("Árbol a Expresión");

        leftPanel.add(btnExpresionAArbol);
        leftPanel.add(Box.createVerticalStrut(20));  // Espacio entre botones
        leftPanel.add(btnArbolAExpresion);

        // Panel principal (contenido que cambiará)
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JLabel label = new JLabel("Seleccione una opción desde el menú.", JLabel.CENTER);
        mainPanel.add(label, BorderLayout.CENTER);

        // Panel dividido (con el sidebar a la izquierda y el panel principal a la derecha)
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, mainPanel);
        splitPane.setDividerLocation(200);  // Tamaño del sidebar
        splitPane.setEnabled(false); // Evita que el usuario cambie el tamaño del divisor

        // Añadir el SplitPane al JFrame
        add(splitPane);

        // ActionListener para el botón "Expresión a Árbol"
        btnExpresionAArbol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reemplazar el contenido del panel principal con el nuevo gráfico
                mainPanel.removeAll();
                mainPanel.add(new ArbolExpresionGrafico(), BorderLayout.CENTER);
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });

        // ActionListener para el botón "Árbol a Expresión"
        btnArbolAExpresion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                 mainPanel.removeAll();
                 mainPanel.add(new ArbolExpresionInteractivo(), BorderLayout.CENTER);
                 mainPanel.revalidate();
                 mainPanel.repaint();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuInterface frame = new MenuInterface();
            frame.setVisible(true);
        });
    }
}
