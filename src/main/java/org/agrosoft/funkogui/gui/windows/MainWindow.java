package org.agrosoft.funkogui.gui.windows;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.extern.slf4j.Slf4j;
import org.agrosoft.funkogui.client.ApiClient;
import org.agrosoft.funkogui.client.FunkoClient;
import org.agrosoft.funkogui.client.RestClient;
import org.agrosoft.funkogui.gui.utils.FormUtils;
import org.agrosoft.funkogui.model.FunkoDto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class MainWindow extends JFrame {

    private static final String PLACEHOLDER = "Busca por Línea, Descripción o Tipo";

    private final JFrame thisReference;
    private FunkoClient client;
    private ApiClient apliClient;
    private TableRowSorter<DefaultTableModel> sorter;
    private boolean firstRun;
    private Timer searchTimer;
    private Timer firstRunTimer;

    private JPanel contentPane;
    private JScrollPane scrollPane;
    private JTable tblFunkos;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnSalir;
    private JLabel lblTotal;
    private JButton btnExportar;
    private JTextField txtFiltro;

    public MainWindow() {
        log.info("Starting MainWindow");
        this.firstRun = true;//avoid null sorter and force llenaFunkos first call
        RestClient restClient = new RestClient();
        this.client = new FunkoClient(restClient);
        this.apliClient = new ApiClient(restClient);
        this.thisReference = this;
        this.setTitle("Administrador de Funkos");
        this.setSize(1300, 700);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        FormUtils.centrarVentanaEnPantalla(this);
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(contentPane);
        this.llenaFunkos();
        this.agregarBusqueda();
        this.txtFiltro.setText(PLACEHOLDER);
        this.txtFiltro.setForeground(Color.GRAY);

        this.btnSalir.addActionListener(actionEvent -> this.exitApplication());
        this.btnAgregar.addActionListener(actionEvent -> this.openAgregarFunko());
        this.btnEditar.addActionListener(actionEvent -> this.openEditarFunko());
        this.btnExportar.addActionListener(actionEvent -> this.exportarAExcel());
        this.setFirstRun();
    }

    private void setFirstRun() {
        this.firstRunTimer = new Timer(500, e -> {
            firstRun = false;
        });
        this.firstRunTimer.setRepeats(false);
        this.firstRunTimer.start();
    }

    private void exitApplication() {
        log.info("Exit appication in progress");
        this.apliClient.shutdownBackend();
        this.dispose();
    }

    private void exportarAExcel() {
        log.info("Exportar a excel seleccionado. Obteniendo información");
        byte[] data = this.client.exportToExcel();
        if (Objects.isNull(data) || data.length == 0) {
            log.error("No se recibió información. Abortando proceso");
            JOptionPane.showMessageDialog(this, "No se recibió contenido del servidor.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        log.info("Información obtenida");
        Optional<File> optFile = this.promptFileSaveLocation();
        if (optFile.isEmpty()) {
            log.warn("No se seleccionó ninguna ruta para guardar. Abortando proceso");
            return;
        }

        File toSave = optFile.get();
        if (toSave.exists() && !this.confirmOverwrite(toSave)) {
            return;
        }

        log.info("Escribiendo el archivo de salida en {}", toSave.getAbsolutePath());
        try (FileOutputStream fos = new FileOutputStream(toSave)) {
            fos.write(data);
            JOptionPane.showMessageDialog(this,
                    "Archivo guardado correctamente en: " + toSave.getAbsolutePath(),
                    "Archivo guardado!", JOptionPane.INFORMATION_MESSAGE);
            log.info("Archivo guardado. Terminando proceso");
        } catch (Exception e) {
            log.error("Ocurrió una excepción al guardar el archivo: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                    "Error al descargar el archivo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Optional<File> promptFileSaveLocation() {
        log.info("Preguntando al usuario la ubicación para guardar el archivo");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle("Guardar como...");
        fileChooser.setFileFilter(
                new FileNameExtensionFilter("Archivos válidos (xlsx)", "xlsx"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return Optional.empty();
        }

        File selectedFile = fileChooser.getSelectedFile();
        if (!selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
            selectedFile = new File(selectedFile.getAbsolutePath() + ".xlsx");
        }
        return Optional.of(selectedFile);
    }

    private boolean confirmOverwrite(File file) {
        log.info("El archivo ya existe, validando sobreescritura");
        int confirmation = JOptionPane.showConfirmDialog(this, "El archivo <" + file.getName()
                        + "> ya existe. ¿Está seguro que desea sobreescribirlo?", "Confirmar sobreescritura",
                JOptionPane.YES_NO_OPTION);
        return confirmation == JOptionPane.YES_OPTION;
    }

    private void openEditarFunko() {
        if (this.tblFunkos.getSelectedRow() == -1) {
            log.info("EditFunko option selected but no funko selected. No action taken");
            return;
        }
        log.info("EditFunko option selected");

        JFrame editarFunko = new EditaFunko(thisReference, this.getFunkoSelectedAt(this.tblFunkos.getSelectedRow()));
        editarFunko.setVisible(true);
        this.setVisible(false);
    }

    private FunkoDto getFunkoSelectedAt(int index) {
        log.info("Retrieving Selected funko information");
        return FunkoDto.builder()
                .linea(this.tblFunkos.getValueAt(index, 0).toString())
                .descripcion(this.tblFunkos.getValueAt(index, 1).toString())
                .numero(Integer.parseInt(this.tblFunkos.getModel().getValueAt(index, 7).toString()))
                .tamanio(this.tblFunkos.getValueAt(index, 3).toString())
                .categoria(this.tblFunkos.getValueAt(index, 4).toString())
                .tipo(this.tblFunkos.getValueAt(index, 5).toString())
                .comentarios(
                        Objects.isNull(this.tblFunkos.getValueAt(index, 6)) ?
                                null : this.tblFunkos.getValueAt(index, 6).toString()
                )
                .build();
    }

    private void openAgregarFunko() {
        log.info("AddFunko option selected");
        JFrame agregarWindow = new AgregaFunko(thisReference);
        agregarWindow.setVisible(true);
        this.setVisible(false);
    }

    private void llenaFunkos() {
        log.info("Fetching all Funkos to present as table");
        DefaultTableModel dtm = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 2) ? Integer.class : String.class;
            }
        };
        dtm.setColumnIdentifiers(
                new String[]{"Línea", "Descripción", "Número", "Tamaño", "Categoría", "Tipo", "Comentarios", "NumReal"}
        );
        for (FunkoDto dto : this.fetchFunkos()) {
            dtm.addRow(
                    new Object[]{dto.getLinea(), dto.getDescripcion(), (dto.getNumero() < 1) ? null : dto.getNumero(),
                            dto.getTamanio(), dto.getCategoria(), dto.getTipo(), dto.getComentarios(), dto.getNumero()}
            );
        }
        this.btnEditar.setEnabled(dtm.getRowCount() > 0);
        this.tblFunkos.setModel(dtm);
        this.tblFunkos.removeColumn(this.tblFunkos.getColumnModel().getColumn(7));
        this.lblTotal.setText("Total: " + dtm.getRowCount() + " funkos");
        this.sorter = new TableRowSorter<>(dtm);
        this.tblFunkos.setRowSorter(this.sorter);
        FormUtils.redimensionarTabla(this.tblFunkos);
        this.tblFunkos.getTableHeader().setReorderingAllowed(false);
    }

    private void agregarBusqueda() {
        log.info("Configuring search box");
        this.txtFiltro.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                debounce();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                debounce();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                debounce();
            }
        });

        this.txtFiltro.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtFiltro.getText().equals(PLACEHOLDER)) {
                    txtFiltro.setText("");
                    txtFiltro.setForeground(Color.WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtFiltro.getText().isBlank()) {
                    txtFiltro.setText(PLACEHOLDER);
                    txtFiltro.setForeground(Color.GRAY);
                }
            }
        });

        //evitar alerta de backspace cuando el campo está vacío
        this.txtFiltro.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (!txtFiltro.getText().isEmpty()) {
                        txtFiltro.setText("");
                    }
                    e.consume();
                }
            }
        });

        //Configurar timer
        this.searchTimer = new Timer(500, e -> filterTable());
        this.searchTimer.setRepeats(false);
    }

    private void debounce() {
        if (this.searchTimer.isRunning()) {
            this.searchTimer.restart();
        } else {
            this.searchTimer.start();
        }
    }

    private void filterTable() {
        String text = txtFiltro.getText().trim();
        if (text.isEmpty() || text.equals(PLACEHOLDER)) {
            this.sorter.setRowFilter(null);
        } else {
            this.sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1, 5));
        }
    }

    private List<FunkoDto> fetchFunkos() {
        List<FunkoDto> queryResult = this.client.getAll();
        queryResult = (Objects.isNull(queryResult)) ? List.of() : queryResult.stream()
                                                                  .sorted(
                                                                          Comparator.comparing(FunkoDto::getLinea)
                                                                          .thenComparing(FunkoDto::getDescripcion)
                                                                          .thenComparingInt(FunkoDto::getNumero)
                                                                  )
                                                                  .toList();
        return queryResult;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && !this.firstRun) {
            this.llenaFunkos();
        }
        super.setVisible(visible);
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
        contentPane.setLayout(new GridLayoutManager(12, 5, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(1, 0, 7, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        contentPane.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane = new JScrollPane();
        contentPane.add(scrollPane, new GridConstraints(1, 1, 6, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(1100, 650), null, 0, false));
        tblFunkos = new JTable();
        tblFunkos.setAutoResizeMode(0);
        scrollPane.setViewportView(tblFunkos);
        btnAgregar = new JButton();
        btnAgregar.setText("Agregar Funko");
        contentPane.add(btnAgregar, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnEditar = new JButton();
        btnEditar.setText("Editar Funko");
        contentPane.add(btnEditar, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnSalir = new JButton();
        btnSalir.setText("Salir");
        contentPane.add(btnSalir, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        contentPane.add(spacer3, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(20, 20), null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        contentPane.add(spacer4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        contentPane.add(spacer5, new GridConstraints(1, 2, 6, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(20, 500), new Dimension(20, 500), new Dimension(20, 500), 0, false));
        final Spacer spacer6 = new Spacer();
        contentPane.add(spacer6, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(700, 20), new Dimension(700, 20), new Dimension(700, 20), 0, false));
        final Spacer spacer7 = new Spacer();
        contentPane.add(spacer7, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        lblTotal = new JLabel();
        lblTotal.setText("Label");
        contentPane.add(lblTotal, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnExportar = new JButton();
        btnExportar.setText("Exportar");
        contentPane.add(btnExportar, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtFiltro = new JTextField();
        contentPane.add(txtFiltro, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer8 = new Spacer();
        contentPane.add(spacer8, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(20, 20), null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
