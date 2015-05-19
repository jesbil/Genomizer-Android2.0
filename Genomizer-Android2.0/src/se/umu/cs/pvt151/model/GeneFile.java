package se.umu.cs.pvt151.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Object to represent files within experiment.
 * @author erik c11ean
 *
 */
public class GeneFile implements Parcelable {
	
	private String id;
	private String path;
	private String url;
	private String type;
	private String filename;
	private String date;
	private String author;
	private String uploader;
	private String expId;
	private String grVersion;	
	private String fileSize;
	
	private String isPrivate;
	
	/**
	 * initializes a new GeneFile object.
	 */
	public GeneFile() {
		
	}
	
	
	/**
	 * Initializes a new GeneFile object.
	 * The in parameter is a parcel used to
	 * initialize the objects fields.
	 * 
	 * (isn't used in our code but is used by parcelable, must exist)
	 * 
	 * @param in
	 */
	private GeneFile(Parcel in) {
		id = in.readString();
		expId = in.readString();
		type = in.readString();
		filename = in.readString();
		author = in.readString();
		uploader = in.readString();
		isPrivate = in.readString();
		path = in.readString();
		url = in.readString();
		date = in.readString();
		grVersion = in.readString();
		fileSize = in.readString();
	}
	
	/**
	 * 
	 * more information about a file when text view is
	 * clicked
	 * @param file that extra information will
	 * be received from. 
	 */
	@Override
	public String toString() {
		return "Exp id: " + expId +"\n" +
			   "Type: " + type + "\n" + 
			   "GR Version: "+ grVersion + "\n" +
			   "Author: " + author + "\n" + 
			   "Uploaded by: " + uploader + "\n" +
			   "Date: " + date + "\n" +
			   "File size: " + fileSize + "\n" +
			   "Path: " + path;
	}

	
	/**
	 * Gets the path.
	 * 
	 * @return path
	 */
	public String getPath() {
		return path;
	}

	
	/**
	 * Sets the path.
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Gets the fileSize.
	 * 
	 * @return fileSize
	 */
	public String getFileSize() {
		return fileSize;
	}

	
	/**
	 * Sets the fileSize.
	 * 
	 * @param fileSize
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	
	/**
	 * Gets the URL.
	 * 
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	
	/**
	 * Sets the URL.
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	/**
	 * Gets the genomerelease version.
	 * 
	 * @param grVersion
	 */
	public void setGrVersion(String grVersion) {
		this.grVersion = grVersion;
	}
	
	
	/**
	 * Sets the genomerelease version.
	 * 
	 * @return genomerelease version
	 */
	public String getGrVersion() {
		return grVersion;
	}

	
	/**
	 * Gets the date.
	 * 
	 * @return date
	 */
	public String getDate() {
		return date;
	}

	
	/**
	 * Sets the date.
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	
	/**
	 * Gets whether or not the file is private.
	 * 
	 * @return is private
	 */
	public String getIsPrivate() {
		return isPrivate;
	}

	
	/**
	 * Sets whether or not the file is private.
	 * 
	 * @param isPrivate
	 */
	public void setIsPrivate(String isPrivate) {
		this.isPrivate = isPrivate;
	}

	
	/**
	 * Gets the file id.
	 * 
	 * @return file id
	 */
	public String getFileId() {
		return id;
	}

	
	/**
	 * Sets the file id.
	 * 
	 * @param fileId
	 */
	public void setFileId(String fileId) {
		this.id = fileId;
	}

	
	/**
	 * Gets the experiment id.
	 * 
	 * @return experiment id
	 */
	public String getExpId() {
		return expId;
	}

	
	/**
	 * Sets the experiment id.
	 * 
	 * @param expId
	 */
	public void setExpId(String expId) {
		this.expId = expId;
	}

	
	/**
	 * Gets the type.
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	
	/**
	 * Sets the type.
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	
	/**
	 * Gets the name.
	 * 
	 * @return name
	 */
	public String getName() {
		return filename;
	}

	
	/**
	 * Sets the name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.filename = name;
	}

	
	/**
	 * Gets the author.
	 * 
	 * @return author
	 */
	public String getAuthor() {
		return author;
	}

	
	/**
	 * Sets the author.
	 * 
	 * @param author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	
	/**
	 * Gets the user which uploaded the file.
	 * 
	 * @return user
	 */
	public String getUploadedBy() {
		return uploader;
	}

	
	/**
	 * Sets the user which uploaded the file.
	 * 
	 * @param uploadedBy
	 */
	public void setUploadedBy(String uploadedBy) {
		this.uploader = uploadedBy;
	}

	
	/**
	 * Returns 0. This method is called by the system.
	 */
	public int describeContents() {
		return 0;
	}

	
	/**
	 * Writes this object to a parcel.
	 * 
	 */
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(id);
		out.writeString(expId);
		out.writeString(type);
		out.writeString(filename);
		out.writeString(author);
		out.writeString(uploader);
		out.writeString(isPrivate);
		out.writeString(path);
		out.writeString(url);
		out.writeString(date);
		out.writeString(grVersion);
		out.writeString(fileSize);
	}
	

	/**
	 * This object is used to create arrays of GeneFiles.
	 * 
	 * (isn't used in our code but is used by parcelable, must exist)
	 */
	public static final Parcelable.Creator<GeneFile> CREATOR
	= new Parcelable.Creator<GeneFile>() {
		public GeneFile createFromParcel(Parcel in) {
			return new GeneFile(in);
		}

		public GeneFile[] newArray(int size) {
			return new GeneFile[size];
		}
	};

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((expId == null) ? 0 : expId.hashCode());
		result = prime * result
				+ ((fileSize == null) ? 0 : fileSize.hashCode());
		result = prime * result
				+ ((filename == null) ? 0 : filename.hashCode());
		result = prime * result
				+ ((grVersion == null) ? 0 : grVersion.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((isPrivate == null) ? 0 : isPrivate.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((uploader == null) ? 0 : uploader.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneFile other = (GeneFile) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (expId == null) {
			if (other.expId != null)
				return false;
		} else if (!expId.equals(other.expId))
			return false;
		if (fileSize == null) {
			if (other.fileSize != null)
				return false;
		} else if (!fileSize.equals(other.fileSize))
			return false;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (grVersion == null) {
			if (other.grVersion != null)
				return false;
		} else if (!grVersion.equals(other.grVersion))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isPrivate == null) {
			if (other.isPrivate != null)
				return false;
		} else if (!isPrivate.equals(other.isPrivate))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (uploader == null) {
			if (other.uploader != null)
				return false;
		} else if (!uploader.equals(other.uploader))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}