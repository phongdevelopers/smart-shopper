package app.shopper;

import java.util.Iterator;
import java.util.TreeSet;

import android.view.View;
import android.widget.LinearLayout;

public class ItemList {
	TreeSet<Item> itemList =new TreeSet<Item>();

	public void addItem(Item item) {
		// TODO Auto-generated method stub
		itemList.add(item);
	}

	public View display(boolean shoppingList) {
		// TODO Auto-generated method stub
		LinearLayout ll = new LinearLayout(shopper.con);
		ll.setOrientation(1);
		Iterator<Item> iterator = itemList.iterator();
		while(iterator.hasNext()){
			ll.addView(iterator.next().draw(shoppingList));
		}
        return ll;
	}

	public ItemList() {
		super();
	}
	
	public void deleteItem(){}
	
	public void loadItemList(){}
	
	public void saveItemList(){}

}
