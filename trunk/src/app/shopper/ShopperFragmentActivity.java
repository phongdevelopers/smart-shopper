package app.shopper;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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

public class ShopperFragmentActivity extends FragmentActivity implements OnClickListener, OnTabChangeListener, OnCancelListener, OnShowListener, ViewPager.OnPageChangeListener {
    public static class ItemDialogFragment extends DialogFragment {
	
	}

	static Context con;
    ItemList itemList ;
    int layout;
    View selected;
    
    static String tag= "Smart Shopper";
    
    public static void debug(String debug){
		Log.d(tag,debug);
	}

	private static final int DELETE_ID = 11;
    
	private static final int DIALOG_ABOUT = 21;
	private static final int DIALOG_HELP = 22;	
	private static final int DIALOG_NEW = 23;
	
	private static final int MENU_ABOUT = 31;
	private static final int MENU_HELP = 32;
	private static final int MENU_EXPORT = 33;
	private static final int MENU_IMPORT = 34;
	private static final int MENU_REFRESH = 35;
	
	private static final String shoppingList = "shoppingList";
	private static final String itemlist = "itemList";
	
	InputMethodManager imm;    
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private ShoppingListPagerAdapter mPagerAdapter;
	
	Resources res;
	
	String hint = "";
	
	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);		
	    Context con = this.getApplicationContext();
	    res = getResources();
	    ShopperFragmentActivity.con = con;
	    loadItemList();
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		setContentView(R.layout.tabmain);
		initialiseTabHost(savedInstanceState);
		intialiseViewPager();
		if(itemList.getItemList().isEmpty()||((ViewGroup) itemList.display(true)).getChildCount()==0)
			showItemList();
		else 
			showShoppingList();
	}

	
	/**
     * Initialise ViewPager
     */
    private void intialiseViewPager() {

		List<Fragment> fragments = new LinkedList<Fragment>();
		fragments.add(Fragment.instantiate(this, ItemListFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, ShoppingListFragment.class.getName()));
		mPagerAdapter  = new ShoppingListPagerAdapter(super.getSupportFragmentManager(), fragments);
		//
		mViewPager = (ViewPager)super.findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
    }

	/**
	 * Initialise the Tab Host
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        AddTab(this, mTabHost, mTabHost.newTabSpec(itemlist).setIndicator("Item List",res.getDrawable(R.drawable.note)));
        AddTab(this, mTabHost, mTabHost.newTabSpec(shoppingList).setIndicator("Shopping List",res.getDrawable(R.drawable.icon_shopping_cart)));
        // Default to first tab
        //this.onTabChanged("Tab1");
        //
        mTabHost.setOnTabChangedListener(this);
	}

	/**
	 * Add Tab content to the Tabhost
	 * @param activity
	 * @param mTabHost
	 * @param tabSpec
	 * @param clss
	 * @param args
	 */
	private static void AddTab(ShopperFragmentActivity activity, TabHost mTabHost, TabHost.TabSpec tabSpec) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
        mTabHost.addTab(tabSpec);
	}
    
	public void showItemList(){
		mTabHost.setCurrentTabByTag(itemlist);
	}
	public void showShoppingList(){
		mTabHost.setCurrentTabByTag(shoppingList);
	}

	void showNewItem(){
		showDialog(DIALOG_NEW);
	}

	public void updateItemListDisplay(){
		ScrollView sc=(ScrollView) this.findViewById(R.id.ScrollView01);
		if(sc != null){
			sc.removeAllViews();
			View v= itemList.display(false);
			sc.addView(v);	
			layout = R.layout.itemlist;
		}
	}
	
	
	public void updateShoppingListDisplay(){
		ScrollView sc=(ScrollView) this.findViewById(R.id.ScrollView02);
		if(sc != null){
			sc.removeAllViews();
			sc.addView(itemList.display(true));		
		}
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

	@Override
	protected void onStop() {
		super.onStop();
		saveItemList();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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
	public void onShow(DialogInterface dialog) {
		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		if(layout!=R.layout.itemlist){
			if(layout == R.layout.newitem) dismissDialog(DIALOG_NEW);
			showItemList();
			//displayItemList();
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
		case R.layout.itemlist:
			switch(button){
			case R.id.Button01:updateShoppingListDisplay();break;
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

	    menu.add(0, MENU_REFRESH, 0, "Refresh Data");
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
	    case MENU_REFRESH:
	    	updateItemListDisplay();
	    	updateShoppingListDisplay();
	        return true;
	    }
	    return false;
	}

	@Override
	public void onTabChanged(String tabId) {
//		if(tabId == itemlist)
//			displayItemList();
//		else if(tabId == shoppingList)
//			displayShoppingList();
		int pos = this.mTabHost.getCurrentTab();
		this.mViewPager.setCurrentItem(pos);
		//shopper.debug(tabId);
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
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		if(position == 0)
		{
			debug("Item List");
			layout = R.layout.itemlist;
		}
		else if(position == 1)
		{	
			layout = R.layout.shoppinglist;
			debug("Shopping List");
		}
		
		mTabHost.setCurrentTab(position);
	}
	
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

	/**
	 * A simple factory that returns dummy views to the Tabhost
	 * @author mwho
	 */
	class TabFactory implements TabContentFactory {

		private final Context mContext;

	    /**
	     * @param context
	     */
	    public TabFactory(Context context) {
	        mContext = context;
	    }

	    /** (non-Javadoc)
	     * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
	     */
	    public View createTabContent(String tag) {
	        View v = new View(mContext);
	        v.setMinimumWidth(0);
	        v.setMinimumHeight(0);
	        return v;
	    }

	}
}

