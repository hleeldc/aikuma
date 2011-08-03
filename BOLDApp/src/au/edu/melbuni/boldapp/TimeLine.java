package au.edu.melbuni.boldapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;

/*
 * Controller for the current time line.
 * 
 * Note: Could also be called Segments.
 * 
 */
public class TimeLine {
	
	int segmentCounter = 0;
	
	String identifier = null;
	LinearLayout view = null;
	
	List<Segment> segments = new ArrayList<Segment>();
	Segment selectedForPlaying = null;
	Segment selectedForRecording = null;
	
	public TimeLine(Activity activity, String identifier) {
		this.identifier = identifier;
		this.view = (LinearLayout) activity.findViewById(R.id.timeline);
	}
	
	// Delegator methods.
	//
	public Context getContext() {
		return view.getContext();
	}
	public void setSelectedForPlaying(Segment segment) {
		if (selectedForPlaying == segment) {
			this.selectedForPlaying = null;
		}
		this.selectedForPlaying = segment;
	}
	public void setSelectedForRecording(Segment segment) {
		if (selectedForRecording != null) {
			this.selectedForRecording.unselect();
		}
		if (selectedForRecording == segment) {
			this.selectedForRecording = null;
		} else {
			this.selectedForRecording = segment;
			this.selectedForRecording.select();
		}
	}
	public void add(Segment segment) {
		selectedForRecording = segment;
		segment.select();
		segment.addTo(view);
		segments.add(segment);
	}
	public void remove(Segment segment) {
		segment.removeFrom(view);
		segments.remove(segment);
		if (selectedForRecording != null) { selectedForRecording.unselect(); }
		selectedForRecording = null;
	}
	public void startPlaying(Recorder recorder) {
		System.out.println("SaP");
		
		Segment segment = getSelectedForPlaying();
		
		if (segment == null) {
			return;
		}
		
		segment.startPlaying(recorder);
	}
	public void stopPlaying(Recorder recorder) {
		System.out.println("SoP");
		
		Segment segment = getSelectedForPlaying();
		
		if (segment == null) {
			return;
		}
		
		segment.stopPlaying(recorder);
	}
	public void startRecording(Recorder recorder) {
		System.out.println("SaR");
		
		getSelectedForRecording().startRecording(recorder);
	}
	public void stopRecording(Recorder recorder) {
		System.out.println("SoR");
		
		recorder.stopRecording();
		
		// Last recorded will be the next playing.
		//
		this.selectedForPlaying = this.selectedForRecording;
		
		this.selectedForRecording.unselect();
		this.selectedForRecording = null;
	}
	
	// Returns the selected segment.
	//
	protected Segment getSelectedForPlaying() {
		if (selectedForPlaying == null) {
			return segments.get(segments.size()-1);
		}
		
		return selectedForPlaying;
	}
	// Returns the selected segment
	// and if there is none, creates a new one.
	//
	protected Segment getSelectedForRecording() {
		if (selectedForRecording != null) {
			return selectedForRecording;
		}
		
		Segment segment = new Segment(this, segmentCounter++);
		add(segment);
		
		return segment;
	}
	
}