package tjs.tuneramblr.meta.img;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

/**
 * Basic image retrieval implementation
 */
public class BasicImageRequester implements ImageRequester {

	protected static String TAG = "BasicImageRequester";

	private Uri imgUri;
	private ContentResolver contentResolver;

	public BasicImageRequester(ContentResolver contentResolver, Uri imgUri) {
		this.imgUri = imgUri;
		this.contentResolver = contentResolver;
	}

	@Override
	public String retrieveImage() {
		String retVal = "";
		try {

			// pull the image resource from the filesystem
			InputStream imgIs = contentResolver.openInputStream(imgUri);
			Drawable imgD = Drawable.createFromStream(imgIs,
					imgUri.getLastPathSegment());

			// dump a compresses version of that image resource
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			Bitmap bitmap = ((BitmapDrawable) imgD).getBitmap();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao);
			byte[] ba = bao.toByteArray();

			// encode the compressed image resource
			retVal = Base64.encodeToString(ba, Base64.DEFAULT);

		} catch (FileNotFoundException e) {
			Log.e(TAG, "Image File Not Found!");
		}

		return retVal;
	}
}
