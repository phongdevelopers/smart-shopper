package app.shopper;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Item implements OnCheckedChangeListener,Comparable<Item>,OnClickListener{
	String name;
	String quantity,defaultQuantity;
	boolean display=true;
	boolean selected=false;
	boolean oneTime;
	boolean deleteMe= false;
	
	View draw(boolean shoppingList){
		String name =this.name ;
		if(oneTime) name += " (once)";		
		if(shoppingList){
			if(display){
				TextView tView = new TextView(shopper.con);
				tView.setText(name);
				tView.setClickable(true);
				tView.setOnClickListener(this);
				tView.setTextSize(25);
				tView.setLineSpacing(1, 1);
				return tView;
			}else 
				return null;
		}else{
			CheckBox check=new CheckBox(shopper.con);
			check.setText(name);
			check.setChecked(display);
			check.setOnCheckedChangeListener(this);
			return check;
		}		
	}	

	public Item(String name) {
		this(name,false,"1");
	}
	
	public Item(String name,Boolean oneTime){
		this(name,oneTime,"1");		
	}

	public Item(String name, Boolean oneTime,String defaultQuantity ) {
		this.name = name;
		this.defaultQuantity = defaultQuantity;
		quantity = null;
		this.oneTime = oneTime;
	}
	
	public void toBuy(){
		display = true;
	}
	
	public void bought(){
		display =false;
		if(oneTime)deleteMe=true;
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		shopper.debug("name"+ " "+ isChecked);
		display = isChecked;
	}


	@Override
	public int compareTo(Item another) {
		return(name.compareToIgnoreCase(another.name));
	}


	@Override
	public void onClick(View v) {
		bought();
		v.setVisibility(View.GONE);
	}


	public boolean isValid() {
/*		if(oneTime&&!display)
			return false;
		else return true;*/
		return !deleteMe;
	}

	public void store(String itemCode, Editor editor) {
		editor.putString(itemCode+"_name", name);
		editor.putBoolean(itemCode+"_display",display);
		editor.putBoolean(itemCode+"_oneTime",oneTime);
		//add other variables
	}

	public void load(String itemCode, SharedPreferences settings) {
 	   name = settings.getString(itemCode+"_name",null);
 	   display = settings.getBoolean(itemCode+"_display", false);
 	   oneTime = settings.getBoolean(itemCode+"_oneTime", false);
	}
}
