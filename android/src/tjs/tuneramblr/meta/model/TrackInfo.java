package tjs.tuneramblr.meta.model;

public class TrackInfo {

	private String artist;
	private String album;
	private String track;

	// more to come

	/**
	 * Builds a music metadata object
	 * 
	 * @param artist
	 *            the artist value of the most recently played track track
	 * @param album
	 *            the album value of the most recently played track
	 * @param track
	 *            the name of the most recently played track
	 */
	public TrackInfo(String artist, String album, String track) {
		this.artist = artist;
		this.album = album;
		this.track = track;
	}

	/**
	 * The artist meta data
	 * 
	 * @return the artist name
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * The album meta data
	 * 
	 * @return the album name
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * The track meta data
	 * 
	 * @return the track name
	 */
	public String getTrack() {
		return track;
	}

	@Override
	public String toString() {
		return "TrackInfo [artist=" + artist + ", album=" + album + ", track="
				+ track + "]";
	}

}
