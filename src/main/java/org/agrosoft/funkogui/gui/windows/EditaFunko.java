package org.agrosoft.funkogui.gui.windows;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.extern.slf4j.Slf4j;
import org.agrosoft.funkogui.client.FunkoClient;
import org.agrosoft.funkogui.client.RestClient;
import org.agrosoft.funkogui.gui.utils.FormUtils;
import org.agrosoft.funkogui.gui.utils.NumericDocumentListener;
import org.agrosoft.funkogui.model.FunkoDto;
import org.agrosoft.funkogui.model.enums.FunkoTamanio;
import org.agrosoft.funkogui.model.enums.FunkoTipo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class EditaFunko extends JFrame {
    private final JFrame parentForm;
    private FunkoClient client;
    private FunkoDto funkoDto;

    private JPanel contentPane;
    private JTextField txtLinea;
    private JComboBox cbxTamanio;
    private JComboBox cbxTipo;
    private JTextField txtDescripcion;
    private JTextField txtNumero;
    private JTextField txtCategoria;
    private JTextField txtComentarios;
    private JButton btnCancelar;
    private JButton btnAgregar;

    public EditaFunko(JFrame parent, FunkoDto funkoDto) {
        this.parentForm = parent;
        this.client = new FunkoClient(new RestClient());
        this.funkoDto = funkoDto;
        log.info("EditaFunko opened");
        this.setTitle("Editar Funko");
        this.setSize(500, 300);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        FormUtils.centrarVentanaEnPantalla(this);
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(contentPane);
        this.txtNumero.getDocument().addDocumentListener(new NumericDocumentListener(this.txtNumero));

        this.llenaComboBoxes();
        this.setFields();

        this.btnCancelar.addActionListener(actionEvent -> this.regresarAlParent());
        this.btnAgregar.addActionListener(actionEvent -> this.guardaFunko());
    }

    private void regresarAlParent() {
        if (Objects.nonNull(parentForm)) {
            parentForm.setVisible(true);
            setVisible(false);
            log.info("Returning to parent form");
            dispose();
        } else {
            log.info("No parent form found. Exit with code 1");
            System.exit(1);
        }
    }

    private void llenaComboBoxes() {
        log.info("Configuring comboboxes from enums");
        FormUtils.configuraComboBox(
                this.cbxTamanio,
                Arrays.stream(FunkoTamanio.values())
                        .map(FunkoTamanio::getDescripcion)
                        .toList(),
                0
        );
        FormUtils.configuraComboBox(
                this.cbxTipo,
                Arrays.stream(FunkoTipo.values())
                        .map(FunkoTipo::getDescripcion)
                        .toList(),
                0
        );
    }

    private boolean validateDataMissing() {
        log.info("Validating required fields");
        return this.txtDescripcion.getText().isBlank() ||
                this.txtLinea.getText().isBlank() ||
                this.txtNumero.getText().isBlank();
    }

    private void guardaFunko() {
        if (this.validateDataMissing()) {
            log.warn("Validation failed. No action taken");
            return;
        }
        log.info("Validation successfully passed. Updating funko");
        boolean saved = this.client.updateFunko(
                FunkoDto.builder()
                        .linea(this.txtLinea.getText())
                        .descripcion(this.txtDescripcion.getText())
                        .numero(Integer.parseInt(this.txtNumero.getText()))
                        .tamanio(this.cbxTamanio.getSelectedItem().toString())
                        .categoria(this.txtCategoria.getText())
                        .tipo(this.cbxTipo.getSelectedItem().toString())
                        .comentarios(this.txtComentarios.getText().isBlank() ? null : this.txtComentarios.getText())
                        .build()
        );
        if (saved) {
            JOptionPane.showMessageDialog(this, "Funko guardado " +
                            "exitosamente",
                    "Funko guardado", JOptionPane.INFORMATION_MESSAGE);
            this.regresarAlParent();
        } else {
            JOptionPane.showMessageDialog(this, "Hubo un error guardar el funko. Revise" +
                            " los datos e intente nuevamente",
                    "Error al guardar el funko", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setFields() {
        log.info("Setting fields");
        this.txtComentarios.setText(
                Objects.isNull(this.funkoDto.getComentarios()) ? "" : this.funkoDto.getComentarios()
        );
        this.txtNumero.setText(String.valueOf(this.funkoDto.getNumero()));
        this.txtLinea.setText(this.funkoDto.getLinea());
        this.txtDescripcion.setText(this.funkoDto.getDescripcion());
        this.txtCategoria.setText(this.funkoDto.getCategoria());

        this.cbxTamanio.setSelectedItem(this.funkoDto.getTamanio());
        this.cbxTipo.setSelectedItem(this.funkoDto.getTipo());

        this.txtDescripcion.setEnabled(false);
        this.txtNumero.setEnabled(false);
        this.txtLinea.setEnabled(false);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(9, 6, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        contentPane.add(spacer2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Línea");
        contentPane.add(label1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtLinea = new JTextField();
        contentPane.add(txtLinea, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer3 = new Spacer();
        contentPane.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Descripción");
        contentPane.add(label2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Número");
        contentPane.add(label3, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Tamaño");
        contentPane.add(label4, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Categoría");
        contentPane.add(label5, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Tipo");
        contentPane.add(label6, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Comentarios");
        contentPane.add(label7, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        contentPane.add(spacer4, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cbxTamanio = new JComboBox();
        contentPane.add(cbxTamanio, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbxTipo = new JComboBox();
        contentPane.add(cbxTipo, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtDescripcion = new JTextField();
        contentPane.add(txtDescripcion, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtNumero = new JTextField();
        contentPane.add(txtNumero, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtCategoria = new JTextField();
        contentPane.add(txtCategoria, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtComentarios = new JTextField();
        contentPane.add(txtComentarios, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnCancelar = new JButton();
        btnCancelar.setText("Cancelar");
        contentPane.add(btnCancelar, new GridConstraints(7, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAgregar = new JButton();
        btnAgregar.setText("Guardar");
        contentPane.add(btnAgregar, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        contentPane.add(spacer5, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
