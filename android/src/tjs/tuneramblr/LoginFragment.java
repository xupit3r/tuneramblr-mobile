package tjs.tuneramblr;

import tjs.tuneramblr.services.LoginService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {

	private static final String TAG = "LoginFragment";

	protected EditText usernameText;
	protected EditText passwordText;
	protected Button loginButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login, container, false);

		usernameText = (EditText) view.findViewById(R.id.usernameInput);
		passwordText = (EditText) view.findViewById(R.id.passwordInput);
		loginButton = (Button) view.findViewById(R.id.loginButton);

		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = usernameText.getText().toString();
				String password = passwordText.getText().toString();

				// send the login information to the login service
				Intent loginServiceIntent = new Intent(v.getContext(),
						LoginService.class);
				loginServiceIntent.putExtra(
						TuneramblrConstants.EXTRA_USERNAME_KEY, username);
				loginServiceIntent.putExtra(
						TuneramblrConstants.EXTRA_PASSWORD_KEY, password);

			}
		});

		return view;
	}
}
