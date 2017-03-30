package io.goudai.configurer;

import lombok.NonNull;

import java.util.List;

/**
 * Created by freeman on 17/3/27.
 */
public interface Configurer {

	void addAttribute(@NonNull String app, @NonNull String key, String value);

	void deleteAttribute(@NonNull String app, @NonNull String key);

	void update(@NonNull String app, @NonNull String key, String value);

	Entry getAttribute(@NonNull String app, String key);

	List<Entry> getAttributes(@NonNull String app);





}
