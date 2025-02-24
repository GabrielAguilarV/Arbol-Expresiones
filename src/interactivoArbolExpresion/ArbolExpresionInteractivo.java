package interactivoArbolExpresion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Nodo {
    String valor;
    Nodo izquierdo, derecho;

    public Nodo(String valor) {
        this.valor = valor;
        this.izquierdo = this.derecho = null;
    }
}

class ArbolExpresion extends JPanel {
    Nodo raiz;
    Nodo nodoSeleccionado;
    JButton finalizarBtn;

    public ArbolExpresion() {
        setLayout(null);

        // Botón para finalizar la creación del árbol
        finalizarBtn = new JButton("Finalizar");
        finalizarBtn.setBounds(10, 10, 100, 30);
        finalizarBtn.addActionListener(e -> mostrarExpresion());
        add(finalizarBtn);

        // Evento de clic para seleccionar nodos y agregar hijos
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Nodo seleccionado = encontrarNodo(raiz, e.getX(), e.getY(), getWidth() / 2, 80, getWidth() / 4);
                if (seleccionado != null) {
                    nodoSeleccionado = seleccionado;
                    String lado = JOptionPane.showInputDialog("¿Insertar nodo en Izquierdo o Derecho? (I/D)");
                    if (lado != null && (lado.equalsIgnoreCase("I") || lado.equalsIgnoreCase("D"))) {
                        String valor = JOptionPane.showInputDialog("Ingrese el valor del nodo");
                        if (valor != null) {
                            Nodo nuevo = new Nodo(valor);
                            if (lado.equalsIgnoreCase("I") && nodoSeleccionado.izquierdo == null) {
                                nodoSeleccionado.izquierdo = nuevo;
                            } else if (lado.equalsIgnoreCase("D") && nodoSeleccionado.derecho == null) {
                                nodoSeleccionado.derecho = nuevo;
                            } else {
                                JOptionPane.showMessageDialog(null, "Ese lado ya tiene un nodo.");
                            }
                            repaint();
                        }
                    }
                }
            }
        });
    }

    public void setRaiz(Nodo raiz) {
        this.raiz = raiz;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (raiz != null) {
            g.setFont(new Font("Arial", Font.BOLD, 18)); // Configurar fuente antes de usarla
            dibujarArbol(g, getWidth() / 2, 80, raiz, getWidth() / 4);
        }
    }

    private void dibujarArbol(Graphics g, int x, int y, Nodo nodo, int espacio) {
        if (nodo == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));
        if (nodo.izquierdo != null) {
            g.drawLine(x, y, x - espacio, y + 50);
            dibujarArbol(g, x - espacio, y + 50, nodo.izquierdo, espacio / 2);
        }
        if (nodo.derecho != null) {
            g.drawLine(x, y, x + espacio, y + 50);
            dibujarArbol(g, x + espacio, y + 50, nodo.derecho, espacio / 2);
        }

        // Pintar el fondo azul
        g.setColor(Color.BLUE);
        g.fillOval(x - 15, y - 15, 30, 30);
        g.drawOval(x - 15, y - 15, 30, 30);

        // Dibujar el texto
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(nodo.valor), x - 5, y + 5);

        g.setColor(Color.BLACK);

    }

    private Nodo encontrarNodo(Nodo nodo, int clickX, int clickY, int x, int y, int espacio) {
        if (nodo == null) return null;
        if (Math.abs(clickX - x) <= 15 && Math.abs(clickY - y) <= 15) return nodo;
        Nodo izq = encontrarNodo(nodo.izquierdo, clickX, clickY, x - espacio, y + 50, espacio / 2);
        if (izq != null) return izq;
        return encontrarNodo(nodo.derecho, clickX, clickY, x + espacio, y + 50, espacio / 2);
    }

    public String obtenerExpresion(Nodo nodo) {
        if (nodo == null) return "";
        if (nodo.izquierdo == null && nodo.derecho == null) return nodo.valor;
        return "(" + obtenerExpresion(nodo.izquierdo) + " " + nodo.valor + " " + obtenerExpresion(nodo.derecho) + ")";
    }

    public double evaluarExpresion(Nodo nodo) {
        if (nodo == null) return 0;
        if (nodo.izquierdo == null && nodo.derecho == null && esNumero(nodo.valor)) return Double.parseDouble(nodo.valor);
        double izq = evaluarExpresion(nodo.izquierdo);
        double der = evaluarExpresion(nodo.derecho);
        return switch (nodo.valor) {
            case "+" -> izq + der;
            case "-" -> izq - der;
            case "*" -> izq * der;
            case "/" -> izq / der;
            case "√" -> Math.sqrt(der);
            case "^" -> Math.pow(izq, der);
            default -> 0;
        };
    }
    private boolean esNumero(String valor) {
        try {
            Double.parseDouble(valor);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void mostrarExpresion() {
        if (raiz == null) {
            JOptionPane.showMessageDialog(null, "El árbol está vacío.");
            return;
        }
        String expresion = obtenerExpresion(raiz);
        double resultado = evaluarExpresion(raiz);
        JOptionPane.showMessageDialog(null, "Expresión: " + expresion + "\nResultado: " + resultado);
    }
}

public class ArbolExpresionInteractivo extends JPanel{

    private ArbolExpresion panel;

    public ArbolExpresionInteractivo() {
        setLayout(new BorderLayout());

        // Crear el panel de árbol de expresión
        panel = new ArbolExpresion();
        add(panel, BorderLayout.CENTER);

        // Usar invokeLater para asegurarse de que la interfaz gráfica se muestre primero
        SwingUtilities.invokeLater(() -> {
            // Crear el nodo raíz después de que la interfaz gráfica se haya mostrado
            String valorRaiz = JOptionPane.showInputDialog("Ingrese el valor del nodo raíz");
            if (valorRaiz != null) {
                Nodo raiz = new Nodo(valorRaiz);
                panel.setRaiz(raiz);
            }
        });
    }
    public static void main(String[] args) {
        new ArbolExpresionInteractivo();
    }
}
