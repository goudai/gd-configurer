package io.goudai.configurer;

import lombok.*;

/**
 * Created by freeman on 17/3/27.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entry {
	private String key;
	private String value;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Entry)) return false;

		Entry entry = (Entry) o;

		return getKey().equals(entry.getKey());
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}
}
