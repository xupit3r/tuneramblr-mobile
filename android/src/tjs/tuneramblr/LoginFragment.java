/*
 * Copyright 2012 Joe D'Alessandro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

				v.getContext().startService(loginServiceIntent);

			}
		});

		return view;
	}
}
