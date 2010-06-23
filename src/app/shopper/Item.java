package app.shopper;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Item implements OnCheckedChangeListener,Comparable<Item>,OnClickListener{
	String name;
	String quantity,defaultQuantity;
	boolean display=false;
	boolean selected=false;
	CheckBox check;
	
	CheckBox draw(boolean shoppingList){

		CheckBox check=new CheckBox(shopper.con);
		check.setText(name);
		check.setOnCheckedChangeListener(this);
		return check;
	}
	

	public Item(String name) {
		//super();
		this(name,"1");
	}

	public Item(String name, String defaultQuantity ) {
		//super();
		this.name = name;
		this.defaultQuantity = defaultQuantity;
		quantity = null;
		//check=new CheckBox(shopper.con);
		//check.setText(name);
		//check.setOnCheckedChangeListener(this);
	}
	
	public void toBuy(){
		display = true;
	}
	
	public void bought(){
		display =false;
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		Log.d("Smart Shopper", name+ " "+ isChecked);
		display = isChecked;
	}


	@Override
	public int compareTo(Item another) {
		// TODO Auto-generated method stub
		return(name.compareToIgnoreCase(another.name));
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	
}
