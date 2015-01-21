package org.leores.task.app;

import org.leores.task.*;
import org.leores.util.*;

public class ModelHIV extends Task {
	private static final long serialVersionUID = -5346825769535403903L;

	public Double q0, s0, l0, i0, v0;//Initial densities of quiescent, susceptible, latent, infected cells and virus load.
	public Double a, r, ri, b, c, p, nM, theta, beta1, beta2, g, gq, gs, gl, gi, kappa, gv, gx, D, iS;//HIV infection parameters.
	public Double mt;//The maximum time t to calculate (days)
	public Double dt;//The duration of each calculation step (days).
	public Double trtFrom, trtTo;//The start and end time of a treatment.
	public Double trtB1, trtB2, trtG, trtA, trtP;//Parameter values for beta1, beta2, g, a, and p, during the treatment period.
	public Double q, s, l, i, v, n, t, sp, di1, di2, gik; //Status variables

	public static ClassInfo getClassInfo() {
		ClassInfo rtn = new ClassInfo();

		rtn.tClass = ModelHIV.class;
		rtn.name = "HIV Infection Model";
		rtn.version = "1.0";
		rtn.license = "FreeBSD License";
		rtn.author = "Changwang Zhang";
		rtn.email = "mleoking@gmail.com";
		rtn.contact = "Dept. of Computer Science, University College London, Gower Street, London WC1E 6BT, United Kingdom.";
		rtn.description = "Modelling the course of HIV infection.";

		return rtn;
	}

	/**
	 * Using prepRept rather than beforeRep to do the rept initialisation aims
	 * to
	 * prevent beforeRept() from being executed when mpFit() finishes.
	 */
	protected boolean prepRept() {
		q = q0;
		s = s0;
		l = l0;
		i = i0;
		v = v0;
		t = 0d;
		n = q + s + l + i;
		sp = s / n;
		di1 = di2 = 0d;
		gik = 0d;
		return super.prepRept();
	}

	public Double bto0(Double x) {
		Double rtn = x;
		if (x < 1E-12) {//values smaller than 1E-12 will be converted to 0.
			rtn = 0d;
		}
		return rtn;
	}

	public boolean step() {
		boolean rtn = t <= mt;

		if (rtn) {
			double b1 = beta1, b2 = beta2, g_ = g, a_ = a, p_ = p;
			if (t > D && D >= 0) {
				gik = kappa * i / (i + iS) * n / nM;
			}
			if (t < trtTo && t >= trtFrom) {//During the treatment period
				b1 = trtB1;
				b2 = trtB2;
				g_ = trtG;
				a_ = trtA;
				p_ = trtP;
			}
			double n0 = q0 + s0 + l0 + i0;
			double afix = nM / n;

			di1 = dt * (c * s * theta * b1 * i / n);
			di2 = dt * (b2 * s * v);
			double dq = dt * (b + r * s - a_ * afix * q - gq * q);
			double ds = dt * (a_ * afix * q + p_ * s * (1 - n / nM) - r * s - gs * s) - di1 - di2;
			double dl = dt * (ri * i - a_ * afix * l - gl * l);
			double di = dt * (a_ * afix * l - ri * i - gi * i - gik * i) + di1 + di2;
			double dv = dt * (g_ * i - gv * v);

			q = bto0(q + dq);
			s = bto0(s + ds);
			l = bto0(l + dl);
			i = bto0(i + di);
			v = bto0(v + dv);
			n = q + s + l + i;
			sp = s / n;
			t = t + dt;
		}
		return rtn;
	}

}
