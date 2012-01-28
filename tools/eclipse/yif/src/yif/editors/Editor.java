package yif.editors;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import yif.Activator;
import yif.Serializer;
import yif.model.Issue;

/**
 * Editor for yaml issue file files.
 * 
 * @author kgilmer
 *
 */
public class Editor extends EditorPart {

	private static FormToolkit ftk;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

	private Composite maincomp;
	private FileEditorInput input;
	private List<Issue> issues;
	private Text titleField;
	private Text dueField;
	private Text createdField;
	private TableViewer notesField;
	private Text priorityField;

	private TableViewer issueTable;
	private boolean uiCreateComplete = false;

	protected boolean dirty = false;

	private Button removeIssueButton;

	private Button completeField;

	private String projectName;

	private Section detailSection;

	private Text tagsField;

	@Override
	public void doSave(IProgressMonitor monitor) {		
		try {
			Serializer.serialize(issues, new File(input.getFile().getRawLocationURI().getPath()));

			dirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);
			input.getFile().refreshLocal(0, null);
		} catch (IOException e) {
			Activator.getDefault().getLog().log(newErrorLog("Unable to save file.", e));
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(newErrorLog("Unable to refresh workspace.", e));
		}
	}
	
	private static IStatus newErrorLog(String message, Throwable t) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, t);
	}

	@Override
	public void doSaveAs() {
		Activator.getDefault().getLog().log(newErrorLog("Unimplemented.", null));
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.setSite(site);
		super.setInput(input);

		if (input instanceof FileEditorInput) {
			this.input = (FileEditorInput) input;
			projectName = this.input.getFile().getProject().getName();
			IFile file = ((FileEditorInput) input).getFile();

			try {
				issues = Serializer.deserialize(file.getContents());
			} catch (CoreException e) {
				throw new PartInitException(e.getMessage());
			}
		} else {
			Activator.getDefault().getLog().log(newErrorLog("Input must be a file.", null));
		}
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		ftk = new FormToolkit(parent.getDisplay());
		Form form = ftk.createForm(parent);
		
		
		if (projectName == null)
			form.setText("Issues");
		else 
			form.setText("Issues for " + projectName);
		
		maincomp = form.getBody();
		
		GridLayout glayout = new GridLayout(2, false);
		maincomp.setLayout(glayout);

		Composite issueListComp = ftk.createComposite(maincomp);
		issueListComp.setLayout(newMarginlessGridLayout(1, true));
		GridData gdata = new GridData(GridData.FILL_VERTICAL);
		gdata.widthHint = 200;

		issueListComp.setLayoutData(gdata);
		Section section1 = ftk.createSection(issueListComp, Section.TITLE_BAR);
		section1.setText("Issue List");
		section1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		issueTable = new TableViewer(ftk.createTable(issueListComp, SWT.BORDER));
		issueTable.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		issueTable.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public Object[] getElements(Object inputElement) {
				ArrayList<Issue> sortedIssues = new ArrayList<Issue>(issues);
				Collections.sort(sortedIssues, new Comparator<Issue>() {

					@Override
					public int compare(Issue o1, Issue o2) {
						if (o1.isComplete())
							return 1;
						if (o2.isComplete())
							return -1;
						if (o1.getPriority() > o2.getPriority())
							return 1;
						else if (o1.getPriority() < o2.getPriority())
							return -1;
						
						return o1.getTitle().toUpperCase().compareTo(o2.getTitle().toUpperCase());
					}
				});
				
				return sortedIssues.toArray();
			}
		});
		issueTable.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) {				
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {				
				return false;
			}

			@Override
			public void dispose() {				
			}

			@Override
			public void addListener(ILabelProviderListener listener) {				
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				Issue issue = (Issue) element;

				if (columnIndex == 0)
					return issue.getTitle();

				return null;
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				Issue issue = (Issue) element;
				ImageRegistry ir = Activator.getDefault().getImageRegistry();
				if (issue.isComplete())
					return ir.get(Activator.IMAGE_KEY_ACCEPT);
				
				if (issue.getDue() != null && issue.getDue().before(Calendar.getInstance().getTime()))
					return ir.get(Activator.IMAGE_KEY_CLOCK);
				
				return ir.get(Activator.IMAGE_KEY_BULLET_BLUE);
			}
		});

		issueTable.setInput(issues);
		issueTable.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Issue issue = getSelectedIssue();
				removeIssueButton.setEnabled(issue != null);

				if (issue != null) {
					uiCreateComplete = false;
					renderIssueDetail(issue);
					uiCreateComplete = true;
				}
			}
		});
		
		Composite detailComposite = ftk.createComposite(maincomp, SWT.None);
		glayout = newMarginlessGridLayout(3, false);
		glayout.marginLeft = 6;
		detailComposite.setLayout(glayout);
		detailComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		detailSection = ftk.createSection(detailComposite, Section.TITLE_BAR);
		detailSection.setText("");
		gdata = new GridData(GridData.FILL_HORIZONTAL);
		gdata.horizontalSpan = 3;
		detailSection.setLayoutData(gdata);
		
		titleField = createTextField("Title", Activator.IMAGE_KEY_PAGE, detailComposite);
		priorityField = createTextField("Priority", Activator.IMAGE_KEY_ASTRISK, detailComposite);
		tagsField = createTextField("Tags", Activator.IMAGE_KEY_TAG, detailComposite);
		createdField = createTextField("Created", Activator.IMAGE_KEY_DATE_NEXT, detailComposite);
		dueField = createTextField("Due", Activator.IMAGE_KEY_DATE_PREVIOUS, detailComposite);
		completeField = createCheckboxField("Completed", detailComposite);

		notesField = new TableViewer(ftk.createTable(detailComposite, SWT.BORDER));
		notesField.getTable().setLinesVisible(true);
		gdata = new GridData(GridData.FILL_BOTH);
		gdata.horizontalSpan = 3;
		notesField.getTable().setLayoutData(gdata);
		notesField.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public Object[] getElements(Object inputElement) {

				return ((List<?>) inputElement).toArray();
			}
		});

		notesField.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				String note = (String) element;
				return note;
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		ftk.createLabel(detailComposite, "");
		ftk.createLabel(detailComposite, "");
		Button addNoteButton = ftk.createButton(detailComposite, "Add Note...", SWT.None);
		addNoteButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		addNoteButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Issue issue = getSelectedIssue();
				
				if (issue == null)
					return;
				
				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "Note", "Add a note to the issue.", "", null) {

					@Override
					protected int getInputTextStyle() {
						return SWT.MULTI | SWT.BORDER | SWT.V_SCROLL;
					}
				
					@Override
					protected Control createDialogArea(Composite parent) {
						Control res = super.createDialogArea(parent);
						((GridData) this.getText().getLayoutData()).heightHint = 100;
						return res;
					}
				};
				dlg.open();
				if (dlg.getReturnCode() == InputDialog.OK) {
					issue.getNotes().add(dlg.getValue());
					dirty = true;
					firePropertyChange(IEditorPart.PROP_DIRTY);
					notesField.refresh();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Composite listButtonComp = ftk.createComposite(issueListComp, SWT.None);

		listButtonComp.setLayout(newMarginlessGridLayout(2, true));
		gdata = new GridData();
		gdata.widthHint = 200;
		listButtonComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button addIssueButton = ftk.createButton(listButtonComp, "Add", SWT.None);
		addIssueButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addIssueButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Issue i = new Issue();
				int id = getNextId(issues);
				i.setId(id);
				i.setTitle("New Issue " + id);
				Calendar now = Calendar.getInstance();
				i.setCreated(now.getTime());
				now.add(Calendar.DAY_OF_MONTH, 7);
				i.setDue(now.getTime());

				issues.add(i);
				dirty = true;
				firePropertyChange(IEditorPart.PROP_DIRTY);
				issueTable.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		removeIssueButton = ftk.createButton(listButtonComp, "Remove", SWT.None);
		removeIssueButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeIssueButton.setEnabled(false);
		removeIssueButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Issue issue = getSelectedIssue();

				if (issue != null) {
					issues.remove(issue);
					dirty = true;
					firePropertyChange(IEditorPart.PROP_DIRTY);
					issueTable.refresh();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		uiCreateComplete = true;
	}

	private GridLayout newMarginlessGridLayout(int columns, boolean equal) {
		GridLayout glayout = new GridLayout(columns, equal);
		glayout.marginWidth = 0;
		glayout.marginHeight = 0;

		return glayout;
	}

	protected int getNextId(List<Issue> il) {

		int id = 1;

		if (il != null)
			for (Issue issue : il)
				if (issue.getId() >= id)
					id = issue.getId() + 1;

		return id;
	}

	private Text createTextField(String title, String iconKey, Composite detailComposite) {
		Label imgLabel = ftk.createLabel(detailComposite, "");
		imgLabel.setImage(Activator.getDefault().getImageRegistry().get(iconKey));
		
		ftk.createLabel(detailComposite, title + ": ");
		Text text = ftk.createText(detailComposite, "", SWT.BORDER_DOT);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Issue selection = getSelectedIssue();

				if (selection != null && uiCreateComplete) {
					dirty = true;
					firePropertyChange(IEditorPart.PROP_DIRTY);
					updateIssue(selection);
				}
			}
		});

		return text;
	}

	private Button createCheckboxField(String title, Composite detailComposite) {

		Button text = ftk.createButton(detailComposite, title, SWT.CHECK);
		GridData gdata = new GridData(GridData.FILL_HORIZONTAL);
		gdata.horizontalSpan = 3;
		text.setLayoutData(gdata);
		text.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Issue selection = getSelectedIssue();

				if (selection != null && uiCreateComplete) {
					dirty = true;
					firePropertyChange(IEditorPart.PROP_DIRTY);
					updateIssue(selection);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		return text;
	}

	protected void updateIssue(Issue issue) {
		issue.setTitle(titleField.getText());
		try {
			issue.setCreated(sdf.parse(createdField.getText()));
		} catch (ParseException e) {
			
		}
		
		try {
			issue.setDue(sdf.parse(dueField.getText()));
		} catch (ParseException e) {
			
		}
		
		issue.setComplete(completeField.getSelection());
		if (isNotEmpty(priorityField.getText()))
			issue.setPriority(Integer.parseInt(priorityField.getText()));
		issue.setTags(Arrays.asList(tagsField.getText().split(" ")));
		
		issueTable.refresh();
	}

	private boolean isNotEmpty(String text) {
		return text != null && text.trim().length() > 0;
	}

	protected void renderIssueDetail(Issue issue) {
		detailSection.setText(issue.getId() + " - " + issue.getTitle());

		if (issue.getTitle() != null)
			titleField.setText(issue.getTitle());
		else
			titleField.setText("");

		if (issue.getCreated() != null)
			createdField.setText(sdf.format(issue.getCreated()));
		else
			createdField.setText("");

		if (issue.getDue() != null)
			dueField.setText(sdf.format(issue.getDue()));
		else
			dueField.setText("");

		priorityField.setText(Integer.toString(issue.getPriority()));
		tagsField.setText(tagsToString(issue.getTags()));

		completeField.setSelection(issue.isComplete());

		notesField.setInput(issue.getNotes());
	}

	private String tagsToString(List<String> tags) {
		StringBuilder sb = new StringBuilder();
		
		for (String tag : tags) {
			if (tag.trim().length() > 0) {
				sb.append(tag.trim());
				sb.append(' ');
			}
		}
			
		return sb.toString().trim();
	}

	@Override
	public void setFocus() {
		maincomp.setFocus();
	}

	private Issue getSelectedIssue() {
		Object selection = ((IStructuredSelection) issueTable.getSelection()).getFirstElement();
		if (selection != null && selection instanceof Issue) {
			return (Issue) selection;
		}

		return null;
	}
}
