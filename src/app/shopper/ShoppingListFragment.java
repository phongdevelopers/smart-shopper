package app.shopper;

public class ShoppingListFragment extends ShopperFragment {
	@Override
	protected int getLayoutID() {
		return R.layout.shoppinglist;
	}

	@Override
	protected void refreshData(ShopperFragmentActivity parentActivity) {
		parentActivity.updateShoppingListDisplay();		
	}	
}
