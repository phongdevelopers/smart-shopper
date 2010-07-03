package app.shopper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class shopper extends Activity implements OnClickListener {
    static Context con;
    ItemList itemList ;
    int layout;
    View selected;
    static String tag= "Smart Shopper";
    
    private static final int DELETE_ID = 11;
    
	private static final int DIALOG_ABOUT = 21;
	private static final int DIALOG_HELP = 22;
	
	private static final int MENU_ABOUT = 31;
	private static final int MENU_HELP = 32;
	InputMethodManager imm;
    
    
    
	public void displayItemList(){
		//hiding keyboard
		if(layout == R.layout.newitem)
			imm.hideSoftInputFromWindow(this.findViewById(R.id.EditText01).getWindowToken(), 0);
		
		setContentViewCustom(R.layout.main);	
			ScrollView sc=(ScrollView) this.findViewById(R.id.ScrollView01);
			sc.addView(itemList.display(false));	
			registerForContextMenu(itemList.display(false));
			this.findViewById(R.id.Button01).requestFocus();
			this.findViewById(R.id.Button01).setOnClickListener(this);
			this.findViewById(R.id.Button02).setOnClickListener(this);
	}
	
	public void displayShoppingList(){
		setContentViewCustom(R.layout.shoppinglist);
			ScrollView sc=(ScrollView) this.findViewById(R.id.ScrollView01);
			sc.addView(itemList.display(true));		
			this.findViewById(R.id.Button01).requestFocus();
			this.findViewById(R.id.Button01).setOnClickListener(this);
	}
	
	public void displayNewItem(){
		setContentViewCustom(R.layout.newitem);
			this.findViewById(R.id.EditText01).requestFocus();
			
			//pop up keyboard
			imm.showSoftInput(this.findViewById(R.id.EditText01), InputMethodManager.SHOW_FORCED);
			
			this.findViewById(R.id.Button01).setOnClickListener(this);
			this.findViewById(R.id.Button02).setOnClickListener(this);
			this.findViewById(R.id.Button03).setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		if(layout!=R.layout.main)
			displayItemList();
		else
			super.onBackPressed();
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
		if(itemList.itemList.isEmpty()||((ViewGroup) itemList.display(true)).getChildCount()==0)
			displayItemList();
		else 
			displayShoppingList();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		switch(layout){
		case R.layout.main:displayItemList();break;
		case R.layout.newitem:displayNewItem();break;
		case R.layout.shoppinglist:displayShoppingList();break;
		}
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
			case R.id.Button02:displayNewItem();break;
			}
			break;
			
			
		case R.layout.newitem:
			//processing
			if(button == R.id.Button01||button == R.id.Button03){
				name = ((EditText) this.findViewById(R.id.EditText01)).getText().toString();
				if(name.length()>=1)
					itemList.addItem(new Item(name,((CheckBox) this.findViewById(R.id.CheckBox01)).isChecked()));
				//debug(name.length()+"");
			}
			
			//control
			switch(button){
			case R.id.Button01:case R.id.Button02:
				//TODO kill keyboard
				this.findViewById(R.id.EditText01).clearFocus();
				displayItemList();
				break;
			case R.id.Button03:
				displayNewItem();
				((EditText) this.findViewById(R.id.EditText01)).setHint("Added " + name + ". Enter Next.");
				break; 
			}
			break;
			
			
		case R.layout.shoppinglist:
			switch(button){
			case R.id.Button01:displayItemList();break;
			//case R.id.Button02:displayNewItem();break;
			}
			break;
		}		
	}
	
	public void setContentViewCustom(int id){
		setContentView(id);
		layout = id;
	}

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		itemList.saveItemList(editor);
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
	    default:
	        dialog = null;
	    }
	    return dialog;
	}	

	public static void debug(String debug){
		Log.d(tag,debug);
	}
}