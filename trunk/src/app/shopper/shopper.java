package app.shopper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

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
		
	    Context con = this.getApplicationContext();
	    res = getResources();
	    shopper.con = con;
	    itemList =new ItemList(this);
	    loadItemList();
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
		saveItemList();
	}
	
	protected void loadItemList(){
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		itemList.loadItemList(settings);
	}
	protected void saveItemList(){
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit().clear();
		itemList.saveItemList(editor);		
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
	    	exportFile();
	        //showDialog(DIALOG_HELP);
	        return true;
	    case MENU_IMPORT:
	    	importFile();
	        //showDialog(DIALOG_HELP);
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
	
	public String exportFile(){
		String path = exportFile("smartshopper.xml");
		Toast.makeText(this, "Data Saved to "+ path+".", Toast.LENGTH_LONG).show();
		return path;
	}
	public String exportFile(String fileName){
		//Save latest changes if any
		saveItemList();
		//Prepare to read all preferences
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		Set<?> allPrefs = settings.getAll().entrySet();
		Iterator<?> prefIterator = allPrefs.iterator();
		Entry<?, ?> entry;
		
		//XML output preperations
		String path =getExternalFilesDir(null)+"/"+fileName;
        File newxmlfile = new File(path);
        //we have to bind the new file with a FileOutputStream
        FileOutputStream fileos = null;        
        try{
                fileos = new FileOutputStream(newxmlfile);
        }catch(FileNotFoundException e){
                Log.e("FileNotFoundException", "can't create FileOutputStream");
        }
        //we create a XmlSerializer in order to write xml data
        XmlSerializer serializer = Xml.newSerializer();
        
        //Write the XML file
        //Source of info:http://www.anddev.org/write_a_simple_xml_file_in_the_sd_card_using_xmlserializer-t8350.html
        try {
            //we set the FileOutputStream as output for the serializer, using UTF-8 encoding
                    serializer.setOutput(fileos, "UTF-8");
                    //Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
                    serializer.startDocument(null, Boolean.valueOf(true));
                    //set indentation option
                    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                    //start a tag called "root"
                    serializer.startTag(null, "root");
                    //i indent code just to have a view similar to xml-tree
	                    while(prefIterator.hasNext()){
	            			entry = (Entry<?, ?>) prefIterator.next();
	            			//String str = "key:" +entry.getKey()+"\tValue:"+entry.getValue();
	            			//debug(str);

	            			//save each preferences key in a "key" element
	            			serializer.startTag(null, "key");
	            				//Save the name in a nested "name" element
		            			serializer.startTag(null, "name");
		            			serializer.text(entry.getKey().toString());
	                            serializer.endTag(null, "name");
	                            
	                            //Save the datatype in a nested "datatype" element
		            			serializer.startTag(null, "datatype");
		            			serializer.text(entry.getValue().getClass().toString());
	                            serializer.endTag(null, "datatype");	                            

	                            //Save the value in a nested "value" element
		            			serializer.startTag(null, "value");
		            			serializer.text(entry.getValue().toString());
	                            serializer.endTag(null, "value");
                            serializer.endTag(null, "key");
	            		}
                           /* serializer.startTag(null, "child1");
                            serializer.endTag(null, "child1");
                           
                            serializer.startTag(null, "child2");
                            //set an attribute called "attribute" with a "value" for <child2>
                            serializer.attribute(null, "attribute", "value");
                            serializer.endTag(null, "child2");
                   
                            serializer.startTag(null, "child3");
                            //write some text inside <child3>
                            serializer.text("some text inside child3");
                            serializer.endTag(null, "child3");*/
                           
                    serializer.endTag(null, "root");
                    serializer.endDocument();
                    //write xml data into the FileOutputStream
                    serializer.flush();
                    //finally we close the file stream
                    fileos.close();
            } catch (Exception e) {
                    Log.e("Exception","error occurred while creating xml file");
            }
		return path;
	}
	

	public void importFile(){
		//Java DOM : http://tutorials.jenkov.com/java-xml/dom.html

		//Choose the app folder, and if not present root sdcard folder
		String path = getExternalFilesDir(null)+"/smartshopper.xml";
		if(!(new File(path).exists()))
			path = Environment.getExternalStorageDirectory() + "/smartshopper.xml";

		//if still not found pop an error and exit out of the function
		if(!(new File(path).exists())){
			Toast.makeText(this, "XML file \"smartshopper.xml\" not found!", Toast.LENGTH_LONG).show();
			return;
		}
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("file://"+path);	
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		exportFile("smartshopper_bac.xml");
		Toast.makeText(this, "Existing Data Backed up as smartshopper_bac.xml", Toast.LENGTH_LONG).show();
		//clear all keys
		SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit().clear();
		NodeList keys = doc.getElementsByTagName("key");
		String datatype,key,value;
		debug("Found "+keys.getLength() +" keys, importing now.");
		for(int i=0;i<keys.getLength();i++){
			NodeList keyDet = keys.item(i).getChildNodes();
			/*for(int j=0;j<keyDet.getLength();j++){
				debug(keyDet.item(j).getNodeName());
			}
			debug(keyDet.getLength()+"");*/
			key = 		keyDet.item(1).getFirstChild().getNodeValue().trim();
			datatype = 	keyDet.item(3).getFirstChild().getNodeValue().trim();
			value = 	keyDet.item(5).getFirstChild().getNodeValue().trim();
			
			
			if(datatype.equals("class java.lang.Integer"))
				editor.putInt(key,Integer.parseInt(value));
			else if (datatype.equals("class java.lang.Boolean"))
				editor.putBoolean(key,Boolean.parseBoolean(value));
			else if (datatype.equals("class java.lang.String"))
				editor.putString(key,value);
			else
				debug("Unrecognised datatype: "+datatype);
			}
		editor.commit();
		loadItemList();
		refreshView();
		Toast.makeText(this, "Data Imported", Toast.LENGTH_SHORT).show();
	}
}
