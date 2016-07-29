package com.frame.sdk.util;

import android.database.Cursor;
import android.provider.MediaStore;

import com.frame.sdk.app.FrameApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoUtil {
    private static VideoUtil instance;

    private VideoUtil() {
    }

    public static VideoUtil getInstance() {
        if (instance == null)
            instance = new VideoUtil();
        return instance;
    }

    public List<Video> getList() {
        List<Video> list = null;
        Cursor cursor = FrameApplication.getInstance().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);
        if (cursor != null) {
            list = new ArrayList<Video>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String title = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String album = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                String artist = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                String displayName = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String mimeType = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                String path = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                long duration = cursor
                        .getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                long size = cursor
                        .getLong(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                Video video = new Video(id, title, album, artist, displayName, mimeType, path, size, duration);
                list.add(video);
            }
            cursor.close();
        }
        return list;
    }

    public class Video implements Serializable {
        private static final long serialVersionUID = -7920222595800367956L;
        private int id;
        private String title;
        private String album;
        private String artist;
        private String displayName;
        private String mimeType;
        private String path;
        private long size;
        private long duration;
        private String image;
        private boolean isSelect;

        public boolean getIsSelect() {
            return isSelect;
        }

        public void setIsSelect(Boolean isSelect) {
            this.isSelect = isSelect;
        }


        /**
         *
         */
        public Video() {
            super();
        }

        /**
         * @param id
         * @param title
         * @param album
         * @param artist
         * @param displayName
         * @param mimeType
         * @param size
         * @param duration
         */
        public Video(int id, String title, String album, String artist,
                     String displayName, String mimeType, String path, long size,
                     long duration) {
            super();
            this.id = id;
            this.title = title;
            this.album = album;
            this.artist = artist;
            this.displayName = displayName;
            this.mimeType = mimeType;
            this.path = path;
            this.size = size;
            this.duration = duration;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
