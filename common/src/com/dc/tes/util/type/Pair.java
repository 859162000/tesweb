package com.dc.tes.util.type;

import java.io.Serializable;
import java.util.Map;

/**
 * 两个对象的包
 * 
 * @author huangzx
 * 
 * @param <A>
 *            对象A的类型
 * @param <B>
 *            对象B的类型
 */
public class Pair<A, B> implements Map.Entry<A, B>, Serializable {
	private static final long serialVersionUID = 2260974596480153812L;

	private A a;

	private B b;

	public Pair(A a, B b) {
		super();
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return this.a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public B getB() {
		return this.b;
	}

	public void setB(B b) {
		this.b = b;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.a == null) ? 0 : this.a.hashCode());
		result = PRIME * result + ((this.b == null) ? 0 : this.b.hashCode());
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
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		if (this.a == null) {
			if (other.a != null)
				return false;
		} else if (!this.a.equals(other.a))
			return false;
		if (this.b == null) {
			if (other.b != null)
				return false;
		} else if (!this.b.equals(other.b))
			return false;
		return true;
	}

	public String toString() {
		return "A=" + this.a + ", B=" + this.b;
	}

	public A getKey() {
		return this.getA();
	}

	public B getValue() {
		return this.getB();
	}

	public B setValue(B value) {
		B oldV = this.b;
		this.b = value;
		return oldV;
	}
}
