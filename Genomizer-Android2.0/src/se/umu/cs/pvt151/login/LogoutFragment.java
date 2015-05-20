package se.umu.cs.pvt151.login;

import se.umu.cs.pvt151.com.ComHandler;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class LogoutFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ComHandler.logout();
		Intent intent = new Intent(getActivity(),LoginActivity.class);
		startActivity(intent);
	}
}
