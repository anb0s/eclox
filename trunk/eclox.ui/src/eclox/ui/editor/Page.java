package eclox.ui.editor;

import org.eclipse.ui.forms.editor.FormPage;

import eclox.core.doxyfiles.Setting;

public abstract class Page extends FormPage {

	public Page( Editor editor, String id, String title ) {
		super(editor, id, title);
	}
	
	public abstract Setting getCurrentSetting();
	public abstract void setCurrentSetting( Setting setting );
}
