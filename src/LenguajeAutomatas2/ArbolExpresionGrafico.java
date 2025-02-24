package LenguajeAutomatas2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

// Clase Nodo
class Nodo {
    String valor;
    Nodo izquierda, derecha;

    Nodo(String valor) {
        this.valor = valor;
        this.izquierda = this.derecha = null;
    }
}

// Clase ArbolExpresion para construir y evaluar el árbol de la expresión matemática
class ArbolExpresion {
    private Nodo raiz;

    public ArbolExpresion(String expresion) {
        this.raiz = construirArbol(expresion);
    }

    private Nodo construirArbol(String expresion) {
        Stack<Nodo> nodos = new Stack<>();
        Stack<Character> operadores = new Stack<>();
        StringBuilder num = new StringBuilder();

        for (int i = 0; i < expresion.length(); i++) {
            char c = expresion.charAt(i);

            if (Character.isLetterOrDigit(c) || c == '.') {
                num.append(c);
            } else {
                if (num.length() > 0) {
                    nodos.push(new Nodo(num.toString()));
                    num.setLength(0);
                }

                if (c == '(') {
                    operadores.push(c);
                } else if (c == ')') {
                    while (!operadores.isEmpty() && operadores.peek() != '(') {
                        nodos.push(construirSubArbol(nodos, operadores.pop()));
                    }
                    operadores.pop();
                } else if ("+-*/^".indexOf(c) != -1) {
                    while (!operadores.isEmpty() && prioridad(operadores.peek()) >= prioridad(c)) {
                        nodos.push(construirSubArbol(nodos, operadores.pop()));
                    }
                    operadores.push(c);
                }
            }
        }

        if (num.length() > 0) {
            nodos.push(new Nodo(num.toString()));
        }

        while (!operadores.isEmpty()) {
            nodos.push(construirSubArbol(nodos, operadores.pop()));
        }

        return nodos.pop();
    }

    private Nodo construirSubArbol(Stack<Nodo> nodos, char operador) {
        Nodo nodo = new Nodo(String.valueOf(operador));
        nodo.derecha = nodos.pop();
        if (!nodos.isEmpty()) {
            nodo.izquierda = nodos.pop();
        }
        return nodo;
    }

    private int prioridad(char operador) {
        return switch (operador) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            default -> 0;
        };
    }

    public Nodo getRaiz() {
        return raiz;
    }

    public double evaluar() {
        return evaluarNodo(raiz);
    }

    private double evaluarNodo(Nodo nodo) {
        if (nodo == null) return 0;

        if (nodo.valor.matches("[0-9]+(\\.[0-9]+)?")) {
            return Double.parseDouble(nodo.valor);
        }

        double izquierda = evaluarNodo(nodo.izquierda);
        double derecha = evaluarNodo(nodo.derecha);

        return switch (nodo.valor) {
            case "+" -> izquierda + derecha;
            case "-" -> izquierda - derecha;
            case "*" -> izquierda * derecha;
            case "/" -> derecha != 0 ? izquierda / derecha : Double.NaN;
            case "^" -> Math.pow(izquierda, derecha);
            default -> 0;
        };
    }
}
class PanelArbol extends JPanel {
    private final Nodo raiz;

    public PanelArbol(Nodo raiz) {
        this.raiz = raiz;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (raiz != null) {
            g.setFont(new Font("Arial", Font.BOLD, 18)); // Configurar fuente antes de usarla
            dibujarArbol(g, getWidth() / 2, 50, raiz, getWidth() / 4);
        }
    }

    private void dibujarArbol(Graphics g, int x, int y, Nodo nodo, int desplazamiento) {
        if (nodo == null) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        int nuevoDesplazamiento = Math.max(desplazamiento / 2, 20); // Evitar desplazamiento cero
        if (nodo.izquierda != null) {
            g.drawLine(x, y, x - nuevoDesplazamiento, y + 50);
            dibujarArbol(g, x - nuevoDesplazamiento, y + 50, nodo.izquierda, nuevoDesplazamiento);
        }
        if (nodo.derecha != null) {
            g.drawLine(x, y, x + nuevoDesplazamiento, y + 50);
            dibujarArbol(g, x + nuevoDesplazamiento, y + 50, nodo.derecha, nuevoDesplazamiento);
        }
        // Dibujar el nodo
        g.setColor(Color.BLUE);
        g.fillOval(x - 15, y - 15, 30, 30);

        // Dibujar el texto
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(nodo.valor), x - 5, y + 5);

        // Dibujar conexiones
        g.setColor(Color.BLACK);


    }
}

// Clase principal con interfaz gráfica
public class ArbolExpresionGrafico extends JPanel {
    private JTextField inputExpresion;
    private JButton btnDibujar, btnBorrar, btnEvaluar;
    private JLabel lblResultado;
    private JPanel contenedorArbol;

    public ArbolExpresionGrafico() {
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputExpresion = new JTextField(20);
        btnDibujar = new JButton("Dibujar");
        btnBorrar = new JButton("Borrar");
        btnEvaluar = new JButton("Evaluar");
        lblResultado = new JLabel("Resultado: ");

        panelSuperior.add(new JLabel("Expresión:"));
        panelSuperior.add(inputExpresion);
        panelSuperior.add(btnDibujar);
        panelSuperior.add(btnBorrar);
        panelSuperior.add(btnEvaluar);
        panelSuperior.add(lblResultado);

        add(panelSuperior, BorderLayout.NORTH);
        contenedorArbol = new JPanel(new BorderLayout());
        add(contenedorArbol, BorderLayout.CENTER);

        btnDibujar.addActionListener(e -> {
            String expresion = inputExpresion.getText().trim();
            if (!expresion.isEmpty()) {
                ArbolExpresion arbol = new ArbolExpresion(expresion);
                contenedorArbol.removeAll();
                contenedorArbol.add(new PanelArbol(arbol.getRaiz()), BorderLayout.CENTER);
                contenedorArbol.revalidate();
                contenedorArbol.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "Expresión inválida.");
            }
        });

        btnBorrar.addActionListener(e -> {
            inputExpresion.setText("");
            lblResultado.setText("Resultado: ");
            contenedorArbol.removeAll();
            contenedorArbol.revalidate();
            contenedorArbol.repaint();
        });

        btnEvaluar.addActionListener(e -> {
            String expresion = inputExpresion.getText().trim();
            if (!expresion.isEmpty()) {
                ArbolExpresion arbol = new ArbolExpresion(expresion);
                lblResultado.setText("Resultado: " + arbol.evaluar());
            } else {
                JOptionPane.showMessageDialog(null, "Expresión inválida.");
            }
        });
    }


    public static void main(String[] args) {
        new ArbolExpresionGrafico();
    }
}

//8+(1*(3^2))-(6/4)+(21*4√16)
//(((8+1(1*(3^2)))-(6/4))+(21*(4√16)))
//(a+b*c)+((d*e+h)*g)