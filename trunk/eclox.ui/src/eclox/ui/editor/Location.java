package eclox.ui.editor;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.NavigationLocation;

/**
 * Implements the navigation location used for the doxyfile editor.
 * 
 * @author Guillaume Brocker
 */
public class Location extends NavigationLocation {
	
	private final String PAGE_KEY		= "page";
	private final String SETTING_KEY	= "setting";
	
	private String page;
	private String setting;
	
	public Location( Editor editor ) {
		super( editor );
		
		this.page = editor.getActivePageInstance().getId();
	}

	public boolean mergeInto(INavigationLocation currentLocation) {
		// TODO Auto-generated method stub
		return false;
	}

	public void restoreLocation() {
		// TODO Auto-generated method stub
		
	}

	public void restoreState(IMemento memento) {
		page	= memento.getString(PAGE_KEY);
		setting	= memento.getString(SETTING_KEY);		
	}

	public void saveState(IMemento memento) {
		memento.putString(PAGE_KEY, page);
		memento.putString(SETTING_KEY, setting);		
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}

}
