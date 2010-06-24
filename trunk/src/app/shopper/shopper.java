package app.shopper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
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

	/** Called when the activity is first created. */
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context con = this.getApplicationContext();
        shopper.con = con;
        itemList =new ItemList(this);
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		itemList.loadItemList(settings);
        displayItemList();
    }

	public void displayItemList(){
		setContentViewCustom(R.layout.main);		 
			ScrollView sc=(ScrollView) this.findViewById(R.id.ScrollView01);
			sc.addView(itemList.display(false));	
			registerForContextMenu(itemList.display(false));
			this.findViewById(R.id.Button01).setOnClickListener(this);
			this.findViewById(R.id.Button02).setOnClickListener(this);
	}
	
	public void displayShoppingList(){
		setContentViewCustom(R.layout.shoppinglist);
			ScrollView sc=(ScrollView) this.findViewById(R.id.ScrollView01);
			sc.addView(itemList.display(true));		
			this.findViewById(R.id.Button01).setOnClickListener(this);
			//this.findViewById(R.id.Button02).setOnClickListener(this);
	}
	
	public void displayNewItem(){
		setContentViewCustom(R.layout.newitem);
		this.findViewById(R.id.Button01).setOnClickListener(this);
		this.findViewById(R.id.Button02).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int button = v.getId();
		Log.d(tag, "Layout - " + layout+"  Button - "+button);
		switch(layout){
		case R.layout.main:
			switch(button){
			case R.id.Button01:displayShoppingList();break;
			case R.id.Button02:displayNewItem();break;
			}
			break;
		case R.layout.newitem:
			switch(button){
			case R.id.Button01:
				String name = ((EditText) this.findViewById(R.id.EditText01)).getText().toString();
				if(name.length()>=1)
					itemList.addItem(new Item(name));
				//Log.d(tag, name.length()+"");
				break;
			case R.id.Button02:break;
			}
			displayItemList();break;
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
		//super.onCreateContextMenu(menu, v, menuInfo);
		//menu.add(0, EDIT_ID, 0, "Edit");
		menu.add(0, 01234567, 0,  "Delete");
		selected = v;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Log.d(tag, info==null?"null":"nah");
		switch (item.getItemId()) {
		//case EDIT_ID:
			//editNote(info.id);
			//return true;
		case 01234567:
			selected.setVisibility(View.GONE);
			itemList.deleteItem(itemList.search(((CheckBox)selected).getText()));
			//itemList.deleteItem(info.id);
			//displayItemList();
			return true;
		default:
			//return super.onContextItemSelected(item);
		}
		return false;
	}	
}