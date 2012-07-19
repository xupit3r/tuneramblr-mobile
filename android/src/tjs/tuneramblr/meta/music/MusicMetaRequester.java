package tjs.tuneramblr.meta.music;

import tjs.tuneramblr.meta.model.TrackInfo;

public interface MusicMetaRequester {

	/**
	 * Builds a music meta data object that encapsulates attributes of the most
	 * recently played music track
	 * 
	 * @return music meta data for the most recently played music track
	 */
	public TrackInfo getCurrentTrackInfo();

}
