package app.shopper;

import java.util.Iterator;
import java.util.TreeSet;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class ItemList {
	TreeSet<Item> itemList =new TreeSet<Item>();
	shopper parent;

	public void addItem(Item item) {
		// TODO Auto-generated method stub
		itemList.add(item);
	}

	public View display(boolean shoppingList) {
		// TODO Auto-generated method stub
		LinearLayout ll = new LinearLayout(shopper.con);
		ll.setOrientation(LinearLayout.VERTICAL);
		Iterator<Item> iterator = itemList.iterator();
		while(iterator.hasNext()){
			View v = iterator.next().draw(shoppingList);
			if(v!=null)
			ll.addView(v);
			if(shoppingList==false)
			parent.registerForContextMenu(v);
		}
        return ll;
	}

	public ItemList(shopper shopper) {
		super();
		parent = shopper;
	}
	
	public void deleteItem(long id){
		Iterator<Item> iterator = itemList.iterator();
		for (int i=0;i<=id;i++)
		iterator.next();
		iterator.remove();
	}
	
	public void loadItemList(SharedPreferences settings){
	       int count = settings.getInt("item_count", 0),index =1;
	       if( count ==0) return;
	       Log.d(shopper.tag, count + " records");
	       while(index<=count){
	    	   String itemCode = "item_"+index;
	    	   String name = settings.getString(itemCode+"_name",null);
	    	   boolean display = settings.getBoolean(itemCode+"_display", true);
	    	   
	    	   Item item = new Item(name);
	    	   item.display = display;
	    	   itemList.add(item);
	    	   index++;
	       }
	}
	
	public void saveItemList(Editor editor){		
		editor.putInt("item_count", itemList.size());
		Iterator<Item> iterator = itemList.iterator();
		int index = 1;
		while(iterator.hasNext()){
			Item item = iterator.next();
			String itemCode = "item_"+index;
			
			editor.putString(itemCode+"_name", item.name);
			editor.putBoolean(itemCode+"_display",item.display);
			//add other variables
			
			index++;
		}
		// Commit the edits!
		Log.d(shopper.tag, itemList.size()+" items, saved "+ (index-1)+" records");
		editor.commit();	
	}

}
