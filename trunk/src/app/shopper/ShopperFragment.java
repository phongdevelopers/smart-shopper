package app.shopper;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ShopperFragment extends Fragment {
	
	private ShopperFragmentActivity mParentActivity;
	protected abstract int getLayoutID();
	protected abstract void refreshData(ShopperFragmentActivity parentActivity);
	
	/** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(getLayoutID(), container, false);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mParentActivity = (ShopperFragmentActivity) activity;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		refreshData(mParentActivity);
	}	
}
