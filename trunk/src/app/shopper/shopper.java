package app.shopper;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;

public class shopper extends TabActivity implements OnClickListener, OnTabChangeListener, TabContentFactory, OnCancelListener, OnShowListener {
    static Context con;
    ItemList itemList ;
    int layout;
    View selected;
    
    static String tag= "Smart Shopper";
    
    private static final int DELETE_ID = 11;
    
	private static final int DIALOG_ABOUT = 21;
	private static final int DIALOG_HELP = 22;	
	private static final int DIALOG_NEW = 23;
	
	private static final int MENU_ABOUT = 31;
	private static final int MENU_HELP = 32;
	private static final int MENU_EXPORT = 33;
	private static final int MENU_IMPORT = 34;
	
	private static final String shoppingList = "shoppingList";
	private static final String itemlist = "itemList";
	
	InputMethodManager imm;    
	TabHost tabHost;
	
	Resources res;
	
	String hint = "";
	
	public void setUpTabs(){
	    setContentView(R.layout.tabmain);

	    tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec ;  // Resusable TabSpec for each tab


	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec(itemlist);
	    spec.setIndicator("Item List", res.getDrawable(R.drawable.note));
	    spec.setContent(this);//R.layout.main);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    spec = tabHost.newTabSpec(shoppingList).setIndicator("Shopping List",res.getDrawable(R.drawable.icon_shopping_cart)).setContent(this);//R.layout.shoppinglist);
	    tabHost.addTab(spec);

	    tabHost.setOnTabChangedListener(this);


	}
    
	public void showItemList(){
		showShoppingList();
		tabHost.setCurrentTabByTag(itemlist);
	}
	public void displayItemList(){
		//hiding keyboard
		debug("Item List");
		/*
		if(layout == R.layout.newitem)
			imm.hideSoftInputFromWindow(this.findViewById(R.id.EditText01).getWindowToken(), 0);*/
		/*//tabHost.setCurrentTabByTag("items");
		setContentViewCustom(R.layout.main);	*/
			ScrollView sc=(ScrollView) this.findViewById(R.id.ScrollView01);
			sc.removeAllViews();
			View v= itemList.display(false);
			sc.addView(v);	
			layout = R.layout.main;
			//this.findViewById(R.id.Button01).requestFocus();//Fix For TrackBall
	}
	
	
	public void showShoppingList(){
		tabHost.setCurrentTabByTag(shoppingList);
	}	
	
	public void displayShoppingList(){
		/*setContentViewCustom(R.layout.shoppinglist);

		//tabHost.setCurrentTabByTag("list");
*/
		layout = R.layout.shoppinglist;
		debug("Shopping List");
		ScrollView sc=(ScrollView) this.findViewById(R.id.ScrollView02);
			sc.removeAllViews();
			sc.addView(itemList.display(true));		
			//this.findViewById(R.id.Button01).requestFocus();//Fix For TrackBall
	}
	
	void showNewItem(){
		showDialog(DIALOG_NEW);
	}
	
	public void displayNewItem(Dialog dialog){		
		debug("Dialog");
		dialog.setContentView(R.layout.newitem);
    	dialog.setTitle(R.string.add_item_title);
    	layout=R.layout.newitem;
    	if(hint!="") 
    		((EditText) dialog.findViewById(R.id.EditText01)).setHint(hint);
		dialog.findViewById(R.id.Button03).setOnClickListener(this);
		dialog.findViewById(R.id.Button01).setOnClickListener(this);
		dialog.findViewById(R.id.Button02).setOnClickListener(this);
		dialog.setOnCancelListener(this);
		dialog.setOnShowListener(this);
		hint = "";
	}

	public static void debug(String debug){
		Log.d(tag,debug);
	}

	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);		
	    Context con = this.getApplicationContext();
	    res = getResources();
	    shopper.con = con;
	    loadItemList();
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		setUpTabs();
		if(itemList.getItemList().isEmpty()||((ViewGroup) itemList.display(true)).getChildCount()==0)
			showItemList();
		else 
			showShoppingList();
	}

	@Override
	protected void onStop() {
		super.onStop();
		saveItemList();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		refreshView();
	}

	public void refreshView(){
		switch(layout){
	        case R.layout.main:showItemList();break;
	        case R.layout.newitem:showNewItem();break;//displayNewItem();break;
	        case R.layout.shoppinglist:displayShoppingList();break;
        }
	}
	@Override
	public void onShow(DialogInterface dialog) {
		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		if(layout!=R.layout.main){
			if(layout == R.layout.newitem) dismissDialog(DIALOG_NEW);
			showItemList();
			debug("BackPressed");
		}else
			super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		int button = v.getId();
		debug("Layout - " + layout+"  Button - "+button);
		String name = null;
		switch(layout){
		case R.layout.main:
			switch(button){
			case R.id.Button01:displayShoppingList();break;
			case R.id.Button02:showNewItem();break;
				//displayNewItem();break;
			}
			break;
			
			
		case R.layout.newitem:
			//processing
			if(button == R.id.Button01||button == R.id.Button03){
				name = ((EditText) v.getRootView().findViewById(R.id.EditText01)).getText().toString();
				if(name.length()>=1)
					itemList.addItem(new Item(name,((CheckBox) v.getRootView().findViewById(R.id.CheckBox01)).isChecked()));
				//debug(name.length()+"");
			}
			//removeDialog(DIALOG_NEW);
			//showItemList();			
			//control
			switch(button){
			case R.id.Button01:case R.id.Button02:
				//this.findViewById(R.id.EditText01).clearFocus();
				dismissDialog(DIALOG_NEW);
				/*removeDialog(DIALOG_NEW);*/
				showItemList();
				break;
			case R.id.Button03:
				//displayNewItem();
				if(name.length()>=1)
					hint = "Added " + name + ". Enter Next.";
				showNewItem();
				break; 
			}
			break;			
		
		}	
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		menu.add(0, DELETE_ID, 0,  "Delete");
		selected = v;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		debug(info==null?"menu info is null":"nah");
		if (item.getItemId() == DELETE_ID) {
			selected.setVisibility(View.GONE);
			itemList.deleteItem((String)((CheckBox)selected).getText());
			return true;
		}
		return false;
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	    menu.add(0, MENU_ABOUT, 0, "About");
	    menu.add(0, MENU_HELP, 0, "Help");

	    menu.add(0, MENU_EXPORT, 0, "Export Data");
	    menu.add(0, MENU_IMPORT, 0, "Import Data");
	    return true;

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
	    switch (item.getItemId()) {
	    case MENU_ABOUT:
	        showDialog(DIALOG_ABOUT);
	        return true;
	    case MENU_HELP:
	        showDialog(DIALOG_HELP);
	        return true;
	    case MENU_EXPORT:
	    	saveItemList(true);
	        return true;
	    case MENU_IMPORT:
	    	loadItemList(true);
	        return true;
	    }
	    return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
	    Dialog dialog = new Dialog(this);
	    switch(id) {
	    case DIALOG_ABOUT:
	    	dialog.setContentView(R.layout.about);
	    	dialog.setTitle(R.string.about);
	        break;
	    case DIALOG_HELP:
	    	dialog.setContentView(R.layout.help);
	    	dialog.setTitle(R.string.help);
	        break;
	    case DIALOG_NEW:
	    	displayNewItem(dialog);    	
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}

	@Override
	public void onTabChanged(String tabId) {
		if(tabId == itemlist)
			displayItemList();
		else if(tabId == shoppingList)
			displayShoppingList();
		//shopper.debug(tabId);
	}

	@Override
	public View createTabContent(String tag) {
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(tag == itemlist)
			return inflater.inflate(R.layout.main, null);
		else if(tag == shoppingList)
			return inflater.inflate(R.layout.shoppinglist, null);
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		debug("Prepare dialog " +id);
		if(id == DIALOG_NEW){
			layout = R.layout.newitem;
			displayNewItem(dialog);
		}	else
		super.onPrepareDialog(id, dialog);	
	}
	
	private final String fileName = "smartshopper.xml";
	
	private String getStorageFilePath(Boolean externalStorage){
		return ((externalStorage? getExternalFilesDir(null):getFilesDir()) + "/" + fileName).toString();
	}
	
	private void saveItemList(){saveItemList(false);}
		
	public void saveItemList(Boolean externalStorage)
	{
		String path =getStorageFilePath(externalStorage);
		
		Serializer serializer = new Persister();
   	    
		try {
			// Write to System.out
			serializer.write(itemList, System.out);
			
			// Write to File
			serializer.write(itemList, new File(path));
		} catch (Exception e) {
			e.printStackTrace();
		}	    

		if(externalStorage)
			Toast.makeText(this, "Data Saved to "+ path+".", Toast.LENGTH_LONG).show();
	}
	
	private void loadItemList(){loadItemList(false);}
	
	public void loadItemList(Boolean externalStorage)
	{
		String path = getStorageFilePath(externalStorage);
		if(externalStorage && !(new File(path).exists()))
			path = Environment.getExternalStorageDirectory() + "/"+fileName;

		//if still not found pop an error and exit out of the function
		if(!(new File(path).exists())){
			Toast.makeText(this, "XML file \"smartshopper.xml\" not found!", Toast.LENGTH_LONG).show();
			itemList = new ItemList(this);
			return;
		}
		
		Serializer serializer = new Persister();
		File source = new File(path);

		try {
			itemList = serializer.read(ItemList.class, source);
		    itemList.setParent(this);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
