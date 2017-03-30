package io.goudai.configurer;

import lombok.NonNull;

import java.util.List;

/**
 * Created by freeman on 17/3/27.
 */
public interface Application {

	void deleteApplication(String app);

	void createApplication(String app);

	void updateApplication(String app,String newApp);

	List<String> applicationList();

	void createApp(@NonNull String app, List<Entry> entries);
}

