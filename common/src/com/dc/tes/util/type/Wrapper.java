package com.dc.tes.util.type;

import java.io.Serializable;

/**
 * 一个对象的包
 * 
 * @author huangzx
 * 
 * @param <T>
 *            对象的类型
 */
public class Wrapper<T> implements Serializable {
	private static final long serialVersionUID = 3894875756699947291L;

	private T m_value;

	public Wrapper() {
	}

	public Wrapper(T value) {
		super();
		this.m_value = value;
	}

	public T getValue() {
		return this.m_value;
	}

	public void setValue(T value) {
		this.m_value = value;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.m_value == null) ? 0 : this.m_value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Wrapper<?> other = (Wrapper<?>) obj;
		if (this.m_value == null) {
			if (other.m_value != null)
				return false;
		} else if (!this.m_value.equals(other.m_value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.m_value == null ? "{null}" : this.m_value.toString();
	}
}
