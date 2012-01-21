package yif.model;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author kgilmer
 *
 */
public class Issue {
	private static int lastId = 1;
	private String title;
	private int priority;
	private List<String> tags;
	private Date created;
	private Date due;
	private List<String> notes;
	private boolean complete;
	private int id;
	
	/**
	 * Generates an id and other fields are initilized with emtpy values.
	 */
	public Issue() {
		complete = false;
		created = Calendar.getInstance().getTime();
		tags = new ArrayList<String>();
		notes = new ArrayList<String>();
		
		id = lastId;
		lastId++;		
	}
	
	/**
	 * @return id of issue
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id set id
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return true if issue is complete
	 */
	public boolean isComplete() {
		return complete;
	}
	/**
	 * @param complete set complete
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	/**
	 * @return title of issue
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title set title of issue
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return priority of issue
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority set priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	/**
	 * @return tags associated with issue
	 */
	public List<String> getTags() {
		return tags;
	}
	/**
	 * @param tags set tags
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	/**
	 * @return date created
	 */
	public Date getCreated() {
		return created;
	}
	/**
	 * @param created
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	
	/**
	 * @return due date or null if undefined
	 */
	public Date getDue() {
		return due;
	}
	/**
	 * @param due
	 */
	public void setDue(Date due) {
		this.due = due;
	}
	/**
	 * @return list of notes
	 */
	public List<String> getNotes() {
		return notes;
	}
	/**
	 * @param notes
	 */
	public void setNotes(List<String> notes) {
		this.notes = notes;
	}
	
	@Override
	public String toString() {
		
		return "(" + id + ") " + title;
	}
}
