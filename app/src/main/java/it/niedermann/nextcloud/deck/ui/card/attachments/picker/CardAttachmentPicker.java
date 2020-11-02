package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogAttachmentPickerBinding;
import it.niedermann.nextcloud.deck.ui.branding.Branded;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class CardAttachmentPicker extends BottomSheetDialogFragment implements Branded {

    private DialogAttachmentPickerBinding binding;
    private CardAttachmentPickerListener listener;


    public CardAttachmentPicker() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getParentFragment() instanceof CardAttachmentPickerListener) {
            this.listener = (CardAttachmentPickerListener) getParentFragment();
        } else {
            throw new IllegalArgumentException("Caller must implement " + CardAttachmentPickerListener.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAttachmentPickerBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadAndDisplayContacts();
        loadAndDisplayGallery();
    }

    private void loadAndDisplayGallery() {
        if (SDK_INT >= LOLLIPOP) {
            final ContentResolver contentResolver = requireContext().getContentResolver();
            try (final Cursor outerCursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null)) {
                while (outerCursor.moveToNext()) {
                    Bitmap photo = MediaStore.Images.Thumbnails.getThumbnail(
                            contentResolver, outerCursor.getLong(outerCursor.getColumnIndex(MediaStore.Images.Media._ID)),
                            MediaStore.Images.Thumbnails.MINI_KIND, null);

                    ImageView iv = new ImageView(requireContext());

                    Glide.with(requireContext())
                            .load(photo)
                            .into(iv);
                    binding.thumbnails.addView(iv);
                }
            }
        }
    }

    private void loadAndDisplayContacts() {
        List<Contact> contacts = new ArrayList<>();

        final ContentResolver contentResolver = requireContext().getContentResolver();
        try (final Cursor outerCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)) {
            while (outerCursor.moveToNext()) {
                Contact contact = new Contact();
                contact.userName = outerCursor.getString(outerCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                final String contactId = outerCursor.getString(outerCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?",
                        new String[]{contactId}, null);
                if (pCur.moveToFirst()) {
                    String phone = pCur.getString(
                            pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contact.phoneNumber = phone;
                    DeckLog.info("phoneNumber of " + contact.userName + ": " + phone);
                }
                pCur.close();

                final String lookupKey = outerCursor.getString(outerCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey));

                Bitmap photo = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_person_grey600_24dp);
                if (inputStream != null) {
                    DeckLog.info("FOUND IMAGE for " + contact.userName);
                    photo = BitmapFactory.decodeStream(inputStream);

                    Glide.with(requireContext())
                            .load(photo)
                            .into(binding.avatar);
                    inputStream.close();
                }

                contacts.add(contact);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                contacts.stream().map((c) -> c.userName).collect(Collectors.toList()));

        binding.contactsListView.setAdapter(arrayAdapter);
    }

    private static class Contact {
        String userName;
        String phoneNumber;
        String email;
    }

    public static DialogFragment newInstance() {
        return new CardAttachmentPicker();
    }

    @Override
    public void applyBrand(int mainColor) {}
}
