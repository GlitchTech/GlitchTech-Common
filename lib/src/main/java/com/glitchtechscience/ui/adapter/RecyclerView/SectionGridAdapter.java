package com.glitchtechscience.ui.adapter.RecyclerView;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Adapter to allow use of sections in a RecyclerView using a GridLayoutManager.
 *
 * Modified some from the work Gabriele Mariotti (gabri.mariotti@gmail.com) did.
 */
public abstract class SectionGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final Context context;
	private static final int SECTION_TYPE = 0;

	private boolean isValid = true;
	private RecyclerView.Adapter baseAdapter;
	private SparseArray<Section> sections = new SparseArray<>();

	public SectionGridAdapter( Context ctx, RecyclerView recyclerView, RecyclerView.Adapter adapter ) {

		baseAdapter = adapter;
		context = ctx;

		this.baseAdapter.registerAdapterDataObserver( new RecyclerView.AdapterDataObserver() {

			@Override
			public void onChanged() {

				isValid = baseAdapter.getItemCount() > 0;
				notifyDataSetChanged();
			}

			@Override
			public void onItemRangeChanged( int positionStart, int itemCount ) {

				isValid = baseAdapter.getItemCount() > 0;
				notifyItemRangeChanged( positionStart, itemCount );
			}

			@Override
			public void onItemRangeInserted( int positionStart, int itemCount ) {

				isValid = baseAdapter.getItemCount() > 0;
				notifyItemRangeInserted( positionStart, itemCount );
			}

			@Override
			public void onItemRangeRemoved( int positionStart, int itemCount ) {

				isValid = baseAdapter.getItemCount() > 0;
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

		return context;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int typeView ) {

		if( typeView == SECTION_TYPE ) {

			return getNewSectionViewHolder( parent );
		} else {

			return baseAdapter.onCreateViewHolder( parent, typeView - 1 );
		}
	}

	/**
	 * Return customized subclass of #RecyclerView.ViewHolder
	 *
	 * @param parent
	 * 		ViewGroup
	 *
	 * @return RecyclerView.ViewHolder
	 */
	protected abstract RecyclerView.ViewHolder getNewSectionViewHolder( ViewGroup parent );

	@Override
	public void onBindViewHolder( RecyclerView.ViewHolder sectionViewHolder, int position ) {

		if( isSectionHeaderPosition( position ) ) {

			renderViewHolder( sectionViewHolder, sections.get( position ).title.toString(), position );
		} else {

			baseAdapter.onBindViewHolder( sectionViewHolder, sectionedPositionToPosition( position ) );
		}
	}

	/**
	 * Render the content of Section header.
	 *
	 * @param sectionViewHolder
	 * 		RecyclerView.ViewHolder instanced generated in #getNewSectionViewHolder()
	 * @param title
	 * 		String
	 * @param position
	 * 		int
	 */
	protected abstract void renderViewHolder( RecyclerView.ViewHolder sectionViewHolder, String title, int position );

	/**
	 * Get the item type at the position.
	 *
	 * @param position
	 * 		int
	 *
	 * @return int
	 */
	@Override
	public int getItemViewType( int position ) {

		return isSectionHeaderPosition( position ) ? SECTION_TYPE : baseAdapter.getItemViewType( sectionedPositionToPosition( position ) ) + 1;
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

	/**
	 * Set the sections of this adapter.
	 *
	 * @param sections
	 * 		Section[]
	 */
	public void setSections( Section[] sections ) {

		this.sections.clear();

		Arrays.sort( sections, new Comparator<Section>() {

			@Override
			public int compare( Section o, Section o1 ) {

				return ( o.firstPosition == o1.firstPosition ) ? 0 : ( ( o.firstPosition < o1.firstPosition ) ? -1 : 1 );
			}
		} );

		int offset = 0; // offset positions for the headers we're adding

		for( Section section : sections ) {

			section.sectionedPosition = section.firstPosition + offset;
			this.sections.append( section.sectionedPosition, section );
			++offset;
		}

		notifyDataSetChanged();
	}

	/**
	 * Get position of item, including sections.
	 *
	 * @param position
	 * 		int
	 *
	 * @return int
	 */
	public int positionToSectionedPosition( int position ) {

		int offset = 0;

		for( int i = 0; i < sections.size(); i++ ) {

			if( sections.valueAt( i ).firstPosition > position ) {

				break;
			}

			++offset;
		}

		return position + offset;
	}

	/**
	 * Get position of item, ignoring sections.
	 *
	 * @param sectionedPosition
	 * 		int
	 *
	 * @return int
	 */
	public int sectionedPositionToPosition( int sectionedPosition ) {

		if( isSectionHeaderPosition( sectionedPosition ) ) {

			return RecyclerView.NO_POSITION;
		}

		int offset = 0;

		for( int i = 0; i < sections.size(); i++ ) {

			if( sections.valueAt( i ).sectionedPosition > sectionedPosition ) {

				break;
			}

			--offset;
		}

		return sectionedPosition + offset;
	}

	/**
	 * Returns true if the specified position is a section header
	 *
	 * @param position
	 * 		int
	 *
	 * @return boolean
	 */
	public boolean isSectionHeaderPosition( int position ) {

		return sections.get( position ) != null;
	}

	/**
	 * Returns the item id at the specified position. Calculates an id for section headers.
	 *
	 * @param position
	 * 		int
	 *
	 * @return long
	 */
	@Override
	public long getItemId( int position ) {

		return isSectionHeaderPosition( position ) ? Integer.MAX_VALUE - sections.indexOfKey( position ) : baseAdapter.getItemId( sectionedPositionToPosition( position ) );
	}

	/**
	 * Returns count of items and sections.
	 *
	 * @return int
	 */
	@Override
	public int getItemCount() {

		return ( isValid ? baseAdapter.getItemCount() + sections.size() : 0 );
	}
}
