package app.shopper;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

public class shopper extends TabActivity implements OnClickListener, OnTabChangeListener, TabContentFactory, OnCancelListener {
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
	private static final String shoppingList = "shoppingList";
	private static final String itemlist = "itemList";
	
	InputMethodManager imm;    
	TabHost tabHost;
	
	String hint = "";
	
	public void setUpTabs(){
	    setContentView(R.layout.tabmain);

	    tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec ;  // Resusable TabSpec for each tab


	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec(itemlist);
	    spec.setIndicator("Item List");
	    spec.setContent(this);//R.layout.main);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    spec = tabHost.newTabSpec(shoppingList).setIndicator("Shopping List").setContent(this);//R.layout.shoppinglist);
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
    	

    	/*dialog.findViewById(R.id.EditText01).setOnFocusChangeListener(new View.OnFocusChangeListener() {
    	    @Override
    	    public void onFocusChange(View v, boolean hasFocus) {
    	       // if (hasFocus) {
    	        	imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);	
    	       // }
    	    }
    	});*/
    	//dialog.findViewById(R.id.EditText01).requestFocus();	
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                InputMethodManager.HIDE_IMPLICIT_ONLY); 
		//imm.showSoftInput(dialog.findViewById(R.id.EditText01), InputMethodManager.SHOW_FORCED);			
		dialog.findViewById(R.id.Button03).setOnClickListener(this);
		dialog.findViewById(R.id.Button01).setOnClickListener(this);
		dialog.findViewById(R.id.Button02).setOnClickListener(this);
		dialog.setOnCancelListener(this);
		hint = "";
	}

	public static void debug(String debug){
		Log.d(tag,debug);
	}

	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		
	    Context con = this.getApplicationContext();
	    shopper.con = con;
	    itemList =new ItemList(this);
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		itemList.loadItemList(settings);
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		setUpTabs();
		if(itemList.itemList.isEmpty()||((ViewGroup) itemList.display(true)).getChildCount()==0)
			showItemList();
		else 
			showShoppingList();
	}

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		itemList.saveItemList(editor);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		switch(layout){
		case R.layout.main:showItemList();break;
		case R.layout.newitem:showNewItem();break;//displayNewItem();break;
		case R.layout.shoppinglist:displayShoppingList();break;
		}
	}
@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		onBackPressed();
	}

	//TODO bug pressback on new item then rotate phone
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
		}
		super.onPrepareDialog(id, dialog);		
	}
}