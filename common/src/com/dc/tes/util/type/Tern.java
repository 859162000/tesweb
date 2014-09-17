package com.dc.tes.util.type;

import java.io.Serializable;

/**
 * 三个对象的包
 * 
 * @author huangzx
 * 
 * @param <A>
 *            对象A的类型
 * @param <B>
 *            对象B的类型
 * @param <C>
 *            对象C的类型
 */
public class Tern<A, B, C> implements Serializable {
	private static final long serialVersionUID = 8923037449741928351L;

	private A a;

	private B b;

	private C c;

	public Tern(A a, B b, C c) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
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

	public C getC() {
		return this.c;
	}

	public void setC(C c) {
		this.c = c;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.a == null) ? 0 : this.a.hashCode());
		result = PRIME * result + ((this.b == null) ? 0 : this.b.hashCode());
		result = PRIME * result + ((this.c == null) ? 0 : this.c.hashCode());
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
		final Tern<?, ?, ?> other = (Tern<?, ?, ?>) obj;
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
		if (this.c == null) {
			if (other.c != null)
				return false;
		} else if (!this.c.equals(other.c))
			return false;
		return true;
	}
}
