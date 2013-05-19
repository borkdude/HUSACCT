package husacct.define.presentation.jpanel;

import husacct.ServiceProvider;
import husacct.common.services.IServiceListener;
import husacct.control.presentation.util.DialogUtils;
import husacct.define.presentation.jdialog.AddModuleValuesJDialog;
import husacct.define.presentation.jdialog.WarningDialog;
import husacct.define.presentation.jpopup.ModuletreeContextMenu;
import husacct.define.presentation.moduletree.ModuleTree;
import husacct.define.presentation.utils.UiDialogs;
import husacct.define.task.DefinitionController;
import husacct.define.task.JtreeController;
import husacct.define.task.components.AbstractCombinedComponent;
import husacct.define.task.components.AbstractDefineComponent;
import husacct.define.task.components.LayerComponent;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class ModuleJPanel extends JPanel implements ActionListener, TreeSelectionListener, Observer, IServiceListener, KeyListener {

	private static final long serialVersionUID = 6141711414139061921L;

	private JScrollPane moduleTreeScrollPane;
	private ModuleTree moduleTree;
	
	private JButton newModuleButton = new JButton();
	private JButton moveModuleUpButton = new JButton();
	private JButton removeModuleButton = new JButton();
	private JButton moveModuleDownButton = new JButton();
	
	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem addModuleItem = new JMenuItem();
	private JMenuItem removeModuleItem= new JMenuItem();
	private JMenuItem moveModuleUpItem = new JMenuItem();
	private JMenuItem moveModuleDownItem = new JMenuItem();
	
	public ModuleJPanel() {
		super();
		
		
	}

	public void initGui() {
		DefinitionController.getInstance().addObserver(this);
		BorderLayout modulePanelLayout = new BorderLayout();
		this.setLayout(modulePanelLayout);
		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		this.add(createInnerModulePanel(), BorderLayout.CENTER);
		this.updateModuleTree();
		ServiceProvider.getInstance().getControlService().addServiceListener(this);
		createPopupMenu();
	}
	
	public JPanel createInnerModulePanel() {
		JPanel innerModulePanel = new JPanel();
		BorderLayout innerModulePanelLayout = new BorderLayout();
		innerModulePanel.setLayout(innerModulePanelLayout);
		innerModulePanel.setBorder(BorderFactory.createTitledBorder(ServiceProvider.getInstance().getLocaleService().getTranslatedString("ModuleHierachy")));
		innerModulePanel.add(this.createModuleTreePanel(), BorderLayout.CENTER);
		innerModulePanel.add(this.addButtonPanel(), BorderLayout.SOUTH);
		return innerModulePanel;
	}
		
	private JPanel createModuleTreePanel() {
		JPanel moduleTreePanel = new JPanel();
		
		BorderLayout moduleTreePanelLayout = new BorderLayout();
		moduleTreePanel.setLayout(moduleTreePanelLayout);
		this.createModuleTreeScrollPane();
		moduleTreePanel.add(this.moduleTreeScrollPane, BorderLayout.CENTER);
		
		return moduleTreePanel;
	}
	
	private void createModuleTreeScrollPane() {
		this.moduleTreeScrollPane = new JScrollPane();
		this.moduleTreeScrollPane.setPreferredSize(new java.awt.Dimension(383, 213));
		this.updateModuleTree();
	}
	
	private void createPopupMenu(){
		this.addModuleItem = new JMenuItem(ServiceProvider.getInstance().getLocaleService().getTranslatedString("NewModule"));
		this.addModuleItem.addActionListener(this);
		this.removeModuleItem = new JMenuItem(ServiceProvider.getInstance().getLocaleService().getTranslatedString("RemoveModule"));
		this.removeModuleItem.addActionListener(this);
		this.moveModuleUpItem = new JMenuItem(ServiceProvider.getInstance().getLocaleService().getTranslatedString("MoveUp"));
		this.moveModuleUpItem.addActionListener(this);
		this.moveModuleDownItem = new JMenuItem(ServiceProvider.getInstance().getLocaleService().getTranslatedString("MoveDown"));
		this.moveModuleDownItem.addActionListener(this);
		
		popupMenu.add(addModuleItem);
		popupMenu.add(removeModuleItem);
		popupMenu.add(moveModuleUpItem);
		popupMenu.add(moveModuleDownItem);
	}

	protected JPanel addButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(this.createButtonPanelLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		this.newModuleButton = new JButton();
		buttonPanel.add(this.newModuleButton);
		this.newModuleButton.addActionListener(this);
		this.newModuleButton.addKeyListener(this);
			
		this.moveModuleUpButton = new JButton();
		buttonPanel.add(this.moveModuleUpButton);
		this.moveModuleUpButton.addActionListener(this);
		this.moveModuleUpButton.addKeyListener(this);

		this.removeModuleButton = new JButton();
		buttonPanel.add(this.removeModuleButton);
		this.removeModuleButton.addActionListener(this);
		this.removeModuleButton.addKeyListener(this);

		this.moveModuleDownButton = new JButton();
		buttonPanel.add(this.moveModuleDownButton);
		this.moveModuleDownButton.addActionListener(this);
		this.moveModuleDownButton.addKeyListener(this);
		
		this.setButtonTexts();
		return buttonPanel;
	}
	
	private GridLayout createButtonPanelLayout() {
		GridLayout buttonPanelLayout = new GridLayout(2, 2);
		buttonPanelLayout.setColumns(2);
		buttonPanelLayout.setHgap(5);
		buttonPanelLayout.setVgap(5);
		buttonPanelLayout.setRows(2);
		return buttonPanelLayout;
	}
	
	/**
	 * Observer
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.updateModuleTree();
	}
	
	public void updateModuleTree() {
		AbstractDefineComponent rootComponent = DefinitionController.getInstance().getModuleTreeComponents();
		
		this.moduleTree = new ModuleTree(rootComponent);
		moduleTree.setContextMenu(new ModuletreeContextMenu(this));
	;
		this.moduleTreeScrollPane.setViewportView(this.moduleTree);
		this.moduleTree.addTreeSelectionListener(this);
		this.checkLayerComponentIsSelected();
		
		moduleTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				createPopup(event);
			}
			public void mouseClicked(MouseEvent event) {
				createPopup(event);
			}
			public void mouseEntered(MouseEvent event) {
				createPopup(event);
			}
		});
		
		moduleTree.setSelectedRow(DefinitionController.getInstance().getSelectedModuleId());
		
		for (int i = 0; i < moduleTree.getRowCount(); i++) {
			moduleTree.expandRow(i);
		}
	}
	private void createPopup(MouseEvent event){
		if(SwingUtilities.isRightMouseButton(event)){
			int row = moduleTree.getClosestRowForLocation(event.getX(), event.getY());
			moduleTree.setSelectionRow(row);
			checkLayerComponentIsSelected();
			popupMenu.show(event.getComponent(), event.getX(), event.getY());			
		}
	}
	
	/**
	 * Handling ActionPerformed
	 */
	@Override
	public void actionPerformed(ActionEvent action) {
		if (action.getSource() == this.newModuleButton || action.getSource() == this.addModuleItem ) {
			this.newModule();
		} else if (action.getSource() == this.removeModuleButton  || action.getSource() == this.removeModuleItem ) {
			this.removeModule();
		} else if (action.getSource() == this.moveModuleUpButton  || action.getSource() == this.moveModuleUpItem ) {
			this.moveLayerUp();
		} else if (action.getSource() == this.moveModuleDownButton || action.getSource() == this.moveModuleDownItem ) {
			this.moveLayerDown();
		}
		this.updateModuleTree();
	}
	
	public void newModule() {
		AddModuleValuesJDialog addModuleFrame = new AddModuleValuesJDialog(this);
		DialogUtils.alignCenter(addModuleFrame);
		addModuleFrame.initGUI();
	}
	
	public void removeModule() {
		long moduleId = getSelectedModuleId();
		if (moduleId != -1 && moduleId != 0){
			boolean confirm = UiDialogs.confirmDialog(this, ServiceProvider.getInstance().getLocaleService().getTranslatedString("RemoveConfirm"), ServiceProvider.getInstance().getLocaleService().getTranslatedString("RemovePopupTitle"));
			if (confirm) {
				this.moduleTree.clearSelection();
				DefinitionController.getInstance().removeModuleById(moduleId);
			}
		}
	}
	
	public void moveLayerUp() {
		long layerId = getSelectedModuleId();
		DefinitionController.getInstance().moveLayerUp(layerId);
		this.updateModuleTree();
	}
	
	public void moveLayerDown() {
		long layerId = getSelectedModuleId();
		DefinitionController.getInstance().moveLayerDown(layerId);
		this.updateModuleTree();
	}
	
	private long getSelectedModuleId() {
		long moduleId = -1;
		TreePath path = this.moduleTree.getSelectionPath();
		if (path != null){//returns null if nothing is selected
			AbstractDefineComponent selectedComponent = (AbstractDefineComponent) path.getLastPathComponent();
			moduleId = selectedComponent.getModuleId();
		}
		return moduleId;
	}

	@Override
	public void valueChanged(TreeSelectionEvent event) {
        TreePath path = event.getPath();
        AbstractDefineComponent selectedComponent = (AbstractDefineComponent) path.getLastPathComponent();
        if (selectedComponent.getModuleId() != DefinitionController.getInstance().getSelectedModuleId()){
        	this.updateSelectedModule(selectedComponent.getModuleId());
        }
        this.checkLayerComponentIsSelected();
	}
	
	
	private void updateSelectedModule(long moduleId) {
		DefinitionController.getInstance().setSelectedModuleId(moduleId);
	}
	
	// Has side effects, might wanna change?
	public void checkLayerComponentIsSelected() {
		TreePath path = this.moduleTree.getSelectionPath();
		if(path != null && path.getLastPathComponent() instanceof LayerComponent) {
			this.enableMoveLayerObjects();
		} else {
			this.disableMoveLayerObjects();
		}
	}
	
	public void disableMoveLayerObjects() {
		this.moveModuleDownButton.setEnabled(false);
		this.moveModuleUpButton.setEnabled(false);
		this.moveModuleDownItem.setEnabled(false);
		this.moveModuleUpItem.setEnabled(false);
	}
	
	public void enableMoveLayerObjects() {
		this.moveModuleDownButton.setEnabled(true);
		this.moveModuleUpButton.setEnabled(true);
		this.moveModuleDownItem.setEnabled(true);
		this.moveModuleUpItem.setEnabled(true);
	}
 
	@Override
	public void update() {
		this.setButtonTexts();
	}
	
	private void setButtonTexts() {
		this.newModuleButton.setText(ServiceProvider.getInstance().getLocaleService().getTranslatedString("NewModule"));
		this.moveModuleUpButton.setText(ServiceProvider.getInstance().getLocaleService().getTranslatedString("MoveUp"));
		this.removeModuleButton.setText(ServiceProvider.getInstance().getLocaleService().getTranslatedString("RemoveModule"));
		this.moveModuleDownButton.setText(ServiceProvider.getInstance().getLocaleService().getTranslatedString("MoveDown"));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ENTER){
			if (event.getSource() == this.newModuleButton) {
				this.newModule();
			} else if (event.getSource() == this.removeModuleButton) {
				this.removeModule();
			} else if (event.getSource() == this.moveModuleUpButton) {
				this.moveLayerUp();
			} else if (event.getSource() == this.moveModuleDownButton) {
				this.moveLayerDown();
			}
			this.updateModuleTree();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	
		
		
	}
	

 
}
