/*
 * Copyright (c) 2012 Julien Névo. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *  * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *  * The names of the authors or their institutions shall not
 * be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package aks.jnv.view;

/**
 * Interface for Views of music.
 * 
 * @author Julien Névo
 *
 */
public interface IMusicView {

	/**
	 * Updates the song information on the GUI.
	 * @param author the author of the song.
	 * @param comments the comments of the song.
	 * @param musicName the name of the song.
	 * @param format the format of the song.
	 * @param durationInSeconds the duration of the song, in seconds.
	 */
	void updateSongInformation(String author, String comments, String musicName,
			String format, String durationInSeconds);

	/**
	 * Updates the current position in the song, in seconds.
	 * @param position the current position in the songs, in seconds.
	 */
	void updateCurrentPosition(int position);
	
	/**
	 * The user has made a seek. Accuracy may not be satisfied, according to the song format.
	 * @param position the position in the songs to reach, in seconds.
	 */
	void seek(int position);
	
	/**
	 * Notification from the controller that Pause has been activated.
	 */
	void notifyPause();

	/**
	 * Notification from the controller that Play has been activated.
	 */
	void notifyPlay();

	/**
	 * Notification from the controller that Stop has been activated.
	 */
	void notifyStop();
}
