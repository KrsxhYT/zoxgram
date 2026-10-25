package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import static org.telegram.messenger.LocaleController.getString;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.ZoxgramCloudAccount;
import org.telegram.messenger.ZoxgramNoteRepository;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class ZoxgramSettingsActivity extends UniversalFragment {

    private static final int GOOGLE_SIGN_IN_REQUEST = 0x5a71;

    public static final String KEY_SHOW_CHAT_ID = "zoxgram_show_chat_id";
    public static final String KEY_SHOW_REGISTRATION_DATE = "zoxgram_show_registration_date";
    public static final String KEY_SHOW_CONTACT_NOTES = "zoxgram_show_contact_notes";

    private static final int ROW_SHOW_CHAT_ID = 1;
    private static final int ROW_SHOW_REGISTRATION_DATE = 2;
    private static final int ROW_SHOW_CONTACT_NOTES = 3;
    private static final int ROW_CLOUD_ACCOUNT = 4;

    private String cloudZoxId;

    @Override
    public boolean onFragmentCreate() {
        boolean created = super.onFragmentCreate();
        if (created && FirebaseAuth.getInstance().getCurrentUser() != null) {
            ZoxgramCloudAccount.getOrCreateZoxId(new ZoxgramCloudAccount.Callback() {
                @Override
                public void onReady(String zoxId) {
                    cloudZoxId = zoxId;
                    refreshList();
                }

                @Override
                public void onError(String message) {
                    showCloudError(message);
                }
            });
        }
        return created;
    }

    @Override
    protected CharSequence getTitle() {
        return getString(R.string.ZoxgramSettings);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        SharedPreferences preferences = getMessagesController().getMainSettings();
        items.add(UItem.asHeader(getString(R.string.ZoxgramCloudAccount)));
        String accountRowText;
        String accountRowValue = "";
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            accountRowText = getString(R.string.ZoxgramConnectGoogleAccount);
            accountRowValue = getString(R.string.ZoxgramCloudNotesSignInHint);
        } else {
            accountRowText = getString(R.string.ZoxgramCloudAccountConnected);
            accountRowValue = cloudZoxId == null
                    ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                    : getString(R.string.ZoxgramIdValue, cloudZoxId);
        }
        items.add(UItem.asButton(
                ROW_CLOUD_ACCOUNT,
                R.drawable.msg_link,
                accountRowText,
                accountRowValue
        ));
        items.add(UItem.asShadow(getString(R.string.ZoxgramCloudPrivacyInfo)));

        items.add(UItem.asHeader(getString(R.string.ZoxgramProfileDetails)));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_CHAT_ID, getString(R.string.ZoxgramShowChatId))
                .setChecked(preferences.getBoolean(KEY_SHOW_CHAT_ID, true)));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_REGISTRATION_DATE, getString(R.string.ZoxgramShowRegistrationDate))
                .setChecked(preferences.getBoolean(KEY_SHOW_REGISTRATION_DATE, true)));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_CONTACT_NOTES, getString(R.string.ZoxgramShowContactNotes))
                .setChecked(preferences.getBoolean(KEY_SHOW_CONTACT_NOTES, true)));
        items.add(UItem.asShadow(getString(R.string.ZoxgramSettingsPrivacyInfo)));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ROW_CLOUD_ACCOUNT) {
            handleCloudAccountClick();
            return;
        }
        String preferenceKey;
        switch (item.id) {
            case ROW_SHOW_CHAT_ID:
                preferenceKey = KEY_SHOW_CHAT_ID;
                break;
            case ROW_SHOW_REGISTRATION_DATE:
                preferenceKey = KEY_SHOW_REGISTRATION_DATE;
                break;
            case ROW_SHOW_CONTACT_NOTES:
                preferenceKey = KEY_SHOW_CONTACT_NOTES;
                break;
            default:
                return;
        }

        SharedPreferences preferences = getMessagesController().getMainSettings();
        boolean checked = !preferences.getBoolean(preferenceKey, true);
        preferences.edit().putBoolean(preferenceKey, checked).apply();
        if (view instanceof TextCheckCell2) {
            ((TextCheckCell2) view).getCheckBox().setChecked(checked, true);
        }
        NotificationCenter.getInstance(currentAccount).postNotificationName(
                NotificationCenter.updateInterfaces,
                MessagesController.UPDATE_MASK_ZOXGRAM_PROFILE
        );
    }

    private void handleCloudAccountClick() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            new AlertDialog.Builder(getParentActivity(), resourcesProvider)
                    .setTitle(getString(R.string.ZoxgramCloudAccountConnected))
                    .setMessage(getString(
                            R.string.ZoxgramCloudSignOutConfirm,
                            email == null ? "" : email
                    ))
                    .setNegativeButton(getString(R.string.Cancel), null)
                    .setPositiveButton(getString(R.string.ZoxgramCloudSignOut), (dialog, which) -> signOut())
                    .show();
            return;
        }

        Activity activity = getParentActivity();
        if (activity == null) {
            return;
        }
        try {
            GoogleSignInOptions options = buildGoogleSignInOptions();
            if (options == null) {
                showCloudError(getString(R.string.ZoxgramGoogleSignInUnavailable));
                return;
            }
            GoogleSignInClient client = GoogleSignIn.getClient(activity, options);
            activity.startActivityForResult(client.getSignInIntent(), GOOGLE_SIGN_IN_REQUEST);
        } catch (Exception error) {
            showCloudError(getString(R.string.ZoxgramGoogleSignInUnavailable));
        }
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode != GOOGLE_SIGN_IN_REQUEST || resultCode != Activity.RESULT_OK) {
            return;
        }
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data)
                    .getResult(ApiException.class);
            if (account == null || account.getIdToken() == null) {
                showCloudError(getString(R.string.ZoxgramGoogleSignInUnavailable));
                return;
            }
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> {
                        ZoxgramNoteRepository.clearCache();
                        ZoxgramCloudAccount.getOrCreateZoxId(new ZoxgramCloudAccount.Callback() {
                            @Override
                            public void onReady(String zoxId) {
                                cloudZoxId = zoxId;
                                refreshList();
                                NotificationCenter.getInstance(currentAccount).postNotificationName(
                                        NotificationCenter.updateInterfaces,
                                        MessagesController.UPDATE_MASK_ZOXGRAM_PROFILE
                                );
                                BulletinFactory.of(ZoxgramSettingsActivity.this)
                                        .createSimpleBulletin(
                                                R.raw.contact_check,
                                                getString(R.string.ZoxgramIdValue, zoxId)
                                        )
                                        .show();
                            }

                            @Override
                            public void onError(String message) {
                                showCloudError(message);
                            }
                        });
                    })
                    .addOnFailureListener(error -> showCloudError(
                            error.getLocalizedMessage() == null
                                    ? getString(R.string.ZoxgramGoogleSignInUnavailable)
                                    : error.getLocalizedMessage()
                    ));
        } catch (ApiException error) {
            showCloudError(getString(R.string.ZoxgramGoogleSignInUnavailable));
        }
    }

    private void signOut() {
        Activity activity = getParentActivity();
        FirebaseAuth.getInstance().signOut();
        ZoxgramNoteRepository.clearCache();
        if (activity != null) {
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleSignIn.getClient(activity, options).signOut();
        }
        cloudZoxId = null;
        refreshList();
        NotificationCenter.getInstance(currentAccount).postNotificationName(
                NotificationCenter.updateInterfaces,
                MessagesController.UPDATE_MASK_ZOXGRAM_PROFILE
        );
    }

    private GoogleSignInOptions buildGoogleSignInOptions() {
        Activity activity = getParentActivity();
        if (activity == null) {
            return null;
        }
        int resourceId = activity.getResources().getIdentifier(
                "default_web_client_id",
                "string",
                activity.getPackageName()
        );
        if (resourceId == 0) {
            return null;
        }
        String webClientId = activity.getString(resourceId);
        if (TextUtils.isEmpty(webClientId)) {
            return null;
        }
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();
    }

    private void showCloudError(String message) {
        if (getParentActivity() != null) {
            new AlertDialog.Builder(getParentActivity(), resourcesProvider)
                    .setTitle(getString(R.string.ZoxgramCloudAccount))
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.OK), null)
                    .show();
        }
    }

    private void refreshList() {
        if (listView != null && listView.getAdapter() != null) {
            listView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
