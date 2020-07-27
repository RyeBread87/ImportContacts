package com.example.importcontacts;

import androidx.annotation.NonNull;
import com.example.importcontacts.adapters.ContactImportModelAdapter;
import com.example.importcontacts.databinding.ContactImportModelBinding;
import com.example.importcontacts.models.ContactImportModel;
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

// view holder for ContactImportModel objects, which we bind to the RecyclerView in the ContactImport activity
public class ContactImportViewHolder extends SortedListAdapter.ViewHolder<ContactImportModel> {

    private final ContactImportModelBinding mBinding;

    // called from onCreateViewHolder in ContactImportModelAdapter
    public ContactImportViewHolder(ContactImportModelBinding binding, ContactImportModelAdapter.Listener listener) {
        super(binding.getRoot());
        binding.setListener(listener);
        mBinding = binding;
    }

    @Override
    protected void performBind(@NonNull ContactImportModel item) {
        mBinding.setModel(item);
    }
}
