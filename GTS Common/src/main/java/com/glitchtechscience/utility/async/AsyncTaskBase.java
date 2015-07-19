package com.glitchtechscience.utility.async;

import android.os.AsyncTask;

public class AsyncTaskBase extends AsyncTask<Integer, Integer, Void> {

	protected AsyncTaskCompleteListener<Void> callback;

	public AsyncTaskBase( AsyncTaskCompleteListener<Void> cb ) {

		this.callback = cb;
	}

	/**
	 * Runs on the UI thread before doInBackground(Params...).
	 *
	 * This step is normally used to setup the task, for instance by showing a progress bar in the user interface.
	 */
	@Override
	protected void onPreExecute() {

	}

	/**
	 * This step is used to perform background computation that can take a long time. The parameters of the asynchronous task are passed to this step. The result of the computation must be returned by this step and will be passed back to the last step.
	 * This step can also use publishProgress(Progress...) to publish one or more units of progress. These values are published on the UI thread, in the onProgressUpdate(Progress...) step.
	 */
	@Override
	protected Void doInBackground( Integer... params ) {

		return null;
	}

	/**
	 * Runs on the UI thread after publishProgress(Progress...) is invoked. The specified values are the values passed to publishProgress(Progress...).
	 */
	@Override
	protected void onProgressUpdate( Integer... values ) {

	}

	/**
	 * Runs on the UI thread after doInBackground(Params...). The specified result is the value returned by doInBackground(Params...).
	 *
	 * This method won't be invoked if the task was cancelled.
	 */
	@Override
	protected void onPostExecute( Void result ) {

		callback.onTaskComplete( null );
	}

	public boolean isTaskReady() {

		return this.getStatus() == AsyncTask.Status.PENDING;
	}

	public boolean isTaskRunning() {

		return this.getStatus() == AsyncTask.Status.RUNNING;
	}

	public boolean isTaskCompleted() {

		return this.getStatus() == AsyncTask.Status.FINISHED;
	}
}
