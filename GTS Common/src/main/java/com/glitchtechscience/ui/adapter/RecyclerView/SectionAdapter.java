package com.glitchtechscience.ui.adapter.RecyclerView;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Comparator;

public abstract class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final Context mContext;
	private static final int SECTION_TYPE = 0;

	private boolean mValid = true;
	private RecyclerView.Adapter mBaseAdapter;
	private SparseArray<Section> mSections = new SparseArray<>();

	public SectionAdapter( Context context, RecyclerView recyclerView, RecyclerView.Adapter baseAdapter ) {

		mBaseAdapter = baseAdapter;
		mContext = context;

		mBaseAdapter.registerAdapterDataObserver( new RecyclerView.AdapterDataObserver() {

			@Override
			public void onChanged() {

				mValid = mBaseAdapter.getItemCount() > 0;
				notifyDataSetChanged();
			}

			@Override
			public void onItemRangeChanged( int positionStart, int itemCount ) {

				mValid = mBaseAdapter.getItemCount() > 0;
				notifyItemRangeChanged( positionStart, itemCount );
			}

			@Override
			public void onItemRangeInserted( int positionStart, int itemCount ) {

				mValid = mBaseAdapter.getItemCount() > 0;
				notifyItemRangeInserted( positionStart, itemCount );
			}

			@Override
			public void onItemRangeRemoved( int positionStart, int itemCount ) {

				mValid = mBaseAdapter.getItemCount() > 0;
				notifyItemRangeRemoved( positionStart, itemCount );
			}
		} );

		final GridLayoutManager layoutManager = (GridLayoutManager) ( recyclerView.getLayoutManager() );
		layoutManager.setSpanSizeLookup( new GridLayoutManager.SpanSizeLookup() {

			@Override
			public int getSpanSize( int position ) {

				return ( isSectionHeaderPosition( position ) ) ? layoutManager.getSpanCount() : 1;
			}
		} );
	}

	protected Context getContext() {

		return mContext;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int typeView ) {

		if( typeView == SECTION_TYPE ) {

			return getNewSectionViewHolder( parent );
		} else {

			return mBaseAdapter.onCreateViewHolder( parent, typeView - 1 );
		}
	}

	protected abstract RecyclerView.ViewHolder getNewSectionViewHolder( ViewGroup parent );

	@Override
	public void onBindViewHolder( RecyclerView.ViewHolder sectionViewHolder, int position ) {

		if( isSectionHeaderPosition( position ) ) {

			renderViewHolder( sectionViewHolder, mSections.get( position ).title.toString(), position );
		} else {

			mBaseAdapter.onBindViewHolder( sectionViewHolder, sectionedPositionToPosition( position ) );
		}
	}

	protected abstract void renderViewHolder( RecyclerView.ViewHolder sectionViewHolder, String title, int position );

	@Override
	public int getItemViewType( int position ) {

		return isSectionHeaderPosition( position ) ? SECTION_TYPE : mBaseAdapter.getItemViewType( sectionedPositionToPosition( position ) ) + 1;
	}

	public static class Section {

		int firstPosition;
		int sectionedPosition;
		CharSequence title;

		public Section( int firstPosition, CharSequence title ) {

			this.firstPosition = firstPosition;
			this.title = title;
		}

		public CharSequence getTitle() {

			return title;
		}
	}

	public void setSections( Section[] sections ) {

		mSections.clear();

		Arrays.sort( sections, new Comparator<Section>() {

			@Override
			public int compare( Section o, Section o1 ) {

				return ( o.firstPosition == o1.firstPosition ) ? 0 : ( ( o.firstPosition < o1.firstPosition ) ? -1 : 1 );
			}
		} );

		int offset = 0; // offset positions for the headers we're adding

		for( Section section : sections ) {

			section.sectionedPosition = section.firstPosition + offset;
			mSections.append( section.sectionedPosition, section );
			++offset;
		}

		notifyDataSetChanged();
	}

	public int positionToSectionedPosition( int position ) {

		int offset = 0;

		for( int i = 0; i < mSections.size(); i++ ) {

			if( mSections.valueAt( i ).firstPosition > position ) {

				break;
			}

			++offset;
		}

		return position + offset;
	}

	public int sectionedPositionToPosition( int sectionedPosition ) {

		if( isSectionHeaderPosition( sectionedPosition ) ) {

			return RecyclerView.NO_POSITION;
		}

		int offset = 0;

		for( int i = 0; i < mSections.size(); i++ ) {

			if( mSections.valueAt( i ).sectionedPosition > sectionedPosition ) {

				break;
			}

			--offset;
		}

		return sectionedPosition + offset;
	}

	public boolean isSectionHeaderPosition( int position ) {

		return mSections.get( position ) != null;
	}

	@Override
	public long getItemId( int position ) {

		return isSectionHeaderPosition( position ) ? Integer.MAX_VALUE - mSections.indexOfKey( position ) : mBaseAdapter.getItemId( sectionedPositionToPosition( position ) );
	}

	@Override
	public int getItemCount() {

		return ( mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0 );
	}
}
