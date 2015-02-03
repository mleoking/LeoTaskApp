package org.leores.task.app;

import java.util.ArrayList;
import java.util.List;

import org.leores.math.CurveFitter;
import org.leores.net.Link;
import org.leores.net.Network;
import org.leores.net.Networks;
import org.leores.net.Node;
import org.leores.net.degree.DegreeGenerator;
import org.leores.net.degree.PowerLaw;
import org.leores.net.mod.Configuration;
import org.leores.net.mod.ER;
import org.leores.net.mod.Model;
import org.leores.plot.JGnuplot;
import org.leores.plot.JGnuplot.Plot;
import org.leores.task.Task;
import org.leores.task.Tasks;
import org.leores.task.Taskss;
import org.leores.util.DelimitedReader;
import org.leores.util.U;
import org.leores.util.able.Processable1;
import org.leores.util.able.Processable2;
import org.leores.util.data.DataTable;
import org.leores.util.data.DataTableSet;

public class EpiHybrid extends Task {
	private static final long serialVersionUID = 2834932296831756008L;

	public static Integer[] dl;
	public static String dlFnets;
	public Double[] dp;
	public String fnets;
	public Integer n, N;
	public Double b1, b2, beta_1, beta_2, gamma, alpha;
	public Boolean bWellmix = false;
	public Double e = Math.E;
	public Double r_inf, X_N, R_p;

	public Processable1<Boolean, Node> pa1InNet0 = new Processable1<Boolean, Node>() {
		public Boolean process(Node a) {
			return a.getId() < n;
		}
	};

	public boolean prepTask() {
		boolean rtn = false;
		if (bWellmix) {
			rtn = n != null;
		} else {
			if (dl == null || !dlFnets.equals(fnets)) {
				Network net = Network.createFromFile(fnets, null, null);
				Processable1<Boolean, Node> pa1 = null;
				if (fnets.contains("metpop")) {
					pa1 = pa1InNet0;
				}
				dl = U.toArray(net.getDegreeList(Link.Flag.UNDIRECTED, pa1), Integer.class);
				dlFnets = fnets;
			}
			if (dl != null) {
				rtn = true;
				n = 0;
				for (int i = 0; i < dl.length; i++) {
					n += dl[i];
				}
				dp = new Double[dl.length];
				for (int i = 0; i < dl.length; i++) {
					dp[i] = dl[i] / (double) n;
				}
			}
		}
		b1 = beta_1 * alpha;
		b2 = beta_2 * (1 - alpha);
		return rtn;
	}

	public double pow(double x, double y) {
		return Math.pow(x, y);
	}

	public double log(double x) {
		return Math.log(x);
	}

	public double abs(double x) {
		return Math.abs(x);
	}

	public double g0(double x) {
		double rtn = 0;
		if (bWellmix) {
			rtn = pow(x, n - 1);
		} else {
			for (int i = 0; i < dp.length; i++) {
				rtn += dp[i] * pow(x, i);
			}
		}
		return rtn;
	}

	public double g0d(double x) {
		double rtn = 0;
		if (bWellmix) {
			rtn = (n - 1) * pow(x, n - 2);
		} else {
			for (int i = 1; i < dp.length; i++) {
				rtn += dp[i] * i * pow(x, i - 1);
			}
		}
		return rtn;
	}

	public double vartheta_inf(double theta) {
		double rtn = 0;
		if (bWellmix) {
			rtn = (((g0d(1) * b1 - g0d(1)) * b2 * pow(theta, 2) + (g0d(1) - g0d(1) * b1) * b2 * theta + (b1 * b2 - b1) * n - b1 * b2 + b1) * gamma - g0d(1) * b1 * b2 * pow(theta, 2))
					/ (((b1 * b2 - b1) * n - b1 * b2 + b1) * gamma - b1 * b2 * n + b1 * b2);
		} else {
			rtn = (((b1 * b2 - b1) * g0d(theta) + ((g0d(1) * b1 - g0d(1)) * b2 * theta + (g0d(1) - g0d(1) * b1) * b2) * g0(theta)) * gamma - g0d(1) * b1 * b2 * theta * g0(theta))
					/ ((b1 * b2 - b1) * g0d(theta) * gamma - b1 * b2 * g0d(theta));
		}
		return rtn;
	}

	public double f2(double theta) {
		double rtn = (n - 2) * log(vartheta_inf(theta));
		return rtn;
	}

	public double f(double theta) {
		double rtn = -(g0d(1) * (gamma - b1 * gamma) + b1 * pow(e, f2(theta)) * g0d(theta)) / (g0d(1) * (b1 * (gamma - 1) - gamma));
		return rtn;
	}

	public double fv(double vartheta) {
		double rtn = -(-b2 * gamma + gamma + g0(1) * b2 * pow(vartheta, (n - 2))) / (b2 * (gamma - 1) - gamma);
		return rtn;
	}

	public double fFixedPoint(double x0, double dfmin, double nmax) {
		double rtn = x0, df = dfmin + 1;
		for (int i = 0; i < nmax && df > dfmin; i++) {
			double preRtn = rtn;
			rtn = f(rtn);
			df = abs(preRtn - rtn);
		}
		return rtn;
	}

	public double fvFixedPoint(double x0, double dfmin, double nmax) {
		double rtn = x0, df = dfmin + 1;
		for (int i = 0; i < nmax && df > dfmin; i++) {
			double preRtn = rtn;
			rtn = fv(rtn);
			df = abs(preRtn - rtn);
		}
		return rtn;
	}

	public boolean step() {
		double theta_inf_ = 0, vartheta_inf_ = 0;
		if (b1 > 0) {
			theta_inf_ = fFixedPoint(0.0001, 0.00001, 100);
			vartheta_inf_ = vartheta_inf(theta_inf_);
		} else if (b2 > 0) {
			theta_inf_ = 1;
			vartheta_inf_ = fvFixedPoint(0.0001, 0.00001, 100);
		} else {
			theta_inf_ = 1;
			vartheta_inf_ = 1;
		}
		double s_inf = pow(vartheta_inf_, (n - 1)) * g0(theta_inf_);
		r_inf = 1 - s_inf;
		X_N = (1 - pow(vartheta_inf_, n)) * n * N;
		R_p = N * (1 - pow((1 - 1.0 / N), X_N));
		return false;
	}

	public static Network genNetER(int nNode, double avgDegree) {
		int nLink = (int) (nNode * avgDegree / 2);
		Model mER = new ER(nNode, nLink);
		Network net = mER.genNetwork(null);
		net.saveToFile("net-er(n" + nNode + "ad" + avgDegree + ").dat");
		return net;
	}

	public static Network genNetPL(int n, int dmin, int dmax, double r) {
		DegreeGenerator pl = new PowerLaw(null, dmin, dmax, r);
		Model cm = new Configuration(pl, n);
		Network net = cm.genNetwork(null);
		net.saveToFile("net-cm-pl(d" + dmin + "-" + dmax + "r" + r + "n" + n + ").dat");
		return net;
	}

	public static Network genNetMergedMetaPopSameER(int nPop, int nPopSize, double avgDegree) {
		int nPopLink = (int) (nPopSize * avgDegree / 2);
		Model mER = new ER(nPopSize, nPopLink);
		Networks nets = new Networks();
		for (int i = 0; i < nPop; i++) {
			Network net = mER.genNetwork(null);
			nets.push(net);
		}
		Network net = nets.unite();
		net.saveToFile("net-metpop-merged-" + nPop + "Xer(n" + nPopSize + "ad" + avgDegree + ").dat");
		return net;
	}

	public static Network genNetMergedMetaPopSamePL(int nPop, int nPopSize, int dmin, int dmax, double r) {
		DegreeGenerator pl = new PowerLaw(null, dmin, dmax, r);
		Model model = new Configuration(pl, nPopSize);
		Networks nets = new Networks();
		for (int i = 0; i < nPop; i++) {
			Network net = model.genNetwork(null);
			nets.push(net);
		}
		Network net = nets.unite();
		net.saveToFile("net-metpop-merged-" + nPop + "Xpl(n" + nPopSize + "d" + dmin + "-" + dmax + "r" + r + ").dat");
		return net;
	}

	public static void genNetworks() {
		genNetER(1000, 5);
		genNetPL(1000, 3, 30, 3);
		genNetMergedMetaPopSameER(500, 100, 5);
		genNetMergedMetaPopSamePL(500, 100, 3, 30, 3);
	}

	public static DataTable gdt(DataTableSet dts, String sFile, String title, String[] rowToStart, String[] columnsToRead, String[] validRowPattern) {
		String[] rowToEnd = { "End" };
		DelimitedReader dr = (DelimitedReader) U.newInstance(DelimitedReader.class, sFile);
		dr.prep(rowToStart, columnsToRead, rowToEnd);
		dr.setValidRowPattern(validRowPattern);
		DataTable rtn = dr.readValidDataTable(false, title);
		if (dts != null) {
			dts.add(rtn);
		}
		return rtn;
	}

	public static void drawFigs(String sFile) {
		String sf = sFile, s;

		JGnuplot jg = new JGnuplot();
		jg.terminal = "pdfcairo enhanced dashed;";
		jg.output = "$info$.pdf";
		jg.beforeStyleVar = "lw=4;";
		jg.afterHeader = "unset grid;";

		DataTable dt, dt2;
		DataTableSet dts, dts2;

		Plot p_opta_er = new Plot("fig_opta_er") {
			{
				xlabel = "β_1";
				ylabel = "γ";
				zlabel = "α^*";
				xrange = "[-0.02:1.02]";
				yrange = "[-0.02:1.02]";
				extra2 = "set tics scale 0.2;";
			}
		};
		dts = p_opta_er.addNewDataTableSet(null);
		gdt(dts, sf, "", new String[] { "meta-pop-er-mod-opt@S" }, new String[] { "beta_1", "gamma", "alpha-Max-R_p" }, null);
		jg.execute(p_opta_er, jg.plotDensity);

		Plot p_optRp_er = new Plot("fig_optrp_er") {
			{
				xlabel = "β_1";
				ylabel = "γ";
				zlabel = "R_p^*";
				xrange = "[-0.02:1.02]";
				yrange = "[-0.02:1.02]";
				extra2 = "set tics scale 0.2;set log zcb;set cbrange [0.1:500]";
			}
		};
		dts = p_optRp_er.addNewDataTableSet(null);
		gdt(dts, sf, "", new String[] { "meta-pop-er-mod-opt@S" }, new String[] { "beta_1", "gamma", "Max-R_p" }, null);
		jg.execute(p_optRp_er, jg.plotDensity);

		Plot p_opta_er_barp = new Plot("fig_opta_er_barp") {
			{
				xlabel = "β_1";
				ylabel = "α";
				xrange = "[-0.02:1.02]";
				yrange = "[-0.02:1.02]";
				zlabel = "R_p";
				bp2 = "set xtics in mirror;set ytics in mirror;\n";
				bp2 += "unset key;set view map;\n";
				bp2 += "set cblabel '$zlabel$';\n";
				bp2 += "set size square;\n";
				bp2 += "plot '-' using 1:2:3 with image, '-' with lp ls 2 lw 0 lc rgb 'dark-green';#";
			}
		};
		dts = p_opta_er_barp.addNewDataTableSet(null);
		gdt(dts, sf, "", new String[] { "meta-pop-er-mod-opt@SA" }, new String[] { "beta_1", "alpha", "R_p" }, null);
		dt = gdt(null, sf, "", new String[] { "meta-pop-er-mod-opt@S" }, new String[] { "gamma", "beta_1", "alpha-Max-R_p" }, new String[] { "0.1", "^0\\.\\d$" });
		dt = dt.subColumns(1, 2);
		dts.add(dt);
		jg.execute(p_opta_er_barp, jg.plotImage);

		Plot p_opta_er_bdg = new Plot("fig_opta_er_bdg") {
			{
				xlabel = "β_1/γ";
				ylabel = "α^*";
				extra2 = "set border 1+2+4+8 back ls 101;";
				afterStyleVar2 = "lw1=0;pt1=pt2;ps1=0.5;ps2=0;lw2=6;lc2='orange';";
			}
		};
		dt = gdt(null, sf, "", new String[] { "meta-pop-er-mod-opt@S" }, new String[] { "beta_1", "alpha-Max-R_p", "gamma" }, null);
		dts = p_opta_er_bdg.addNewDataTableSet(null);
		//calculate beta_1/gamma and log(beta_1/gamma), log(alpha_*)
		List<Double> lx2Fit = new ArrayList<Double>(), ly2Fit = new ArrayList<Double>();
		for (int i = 0, mi = dt.nRows(); i < mi; i++) {
			String[] row = (String[]) dt.getRow(i);
			double d0 = Double.parseDouble(row[0]), d1 = Double.parseDouble(row[1]), d2 = Double.parseDouble(row[2]);
			row[0] = d0 / d2 + "";
			if (d1 > 0) {
				lx2Fit.add(Math.log(d0 / d2));
				ly2Fit.add(Math.log(d1));
			}
			dt.setRow(i, row);
		}
		String[] cols = dt.getColNames();
		cols[0] = "beta_1/gamma";
		cols[1] = "alpha*";
		dt.setColNames(cols);
		dt = dt.subColumns(0, 1);//remove the last column "gamma"		
		dts.add(dt);
		//curve fitting
		double[] x = U.parsePrimitiveArray(lx2Fit, new double[0]);
		double[] y = U.parsePrimitiveArray(ly2Fit, new double[0]);
		CurveFitter cf = new CurveFitter(x, y);
		cf.doFit(CurveFitter.STRAIGHT_LINE);
		U.tLog(cf.getResultString());
		//calculate the results according to the fitted curve
		List<Double> lx = U.parseList(Double.class, "0.25:0.1:100");
		List<Double> ly = new ArrayList<Double>();
		double[] p = cf.getParams();
		p[0] = Math.round(p[0] * 100) / 100.0;
		p[1] = Math.round(p[1] * 100) / 100.0;
		for (int i = 0, mi = lx.size(); i < mi; i++) {
			double xi = lx.get(i);
			ly.add(Math.exp(p[0] + p[1] * Math.log(xi)));
		}
		dts.addNewDataTable("", lx, ly);
		//prepare the data for the inset plot which uses the ln ln scale
		dt = dts.get(0).clone();
		dt2 = dts.get(1).clone();
		Processable2<Object[], Integer, Object[]> pa2ln = new Processable2<Object[], Integer, Object[]>() {
			public Object[] process(Integer a, Object[] b) {
				String[] rtn = null;
				double x = Double.parseDouble(b[0] + ""), y = Double.parseDouble(b[1] + "");
				if (x > 0 && y > 0) {
					rtn = new String[] { Math.log(x) + "", Math.log(y) + "" };
				}
				return rtn;
			}
		};
		dt.processRows(pa2ln);//apply ln to x and y values.
		dt2.processRows(pa2ln);//apply ln to x and y values.
		dts = p_opta_er_bdg.addNewDataTableSet(null);
		dts.add(dt, dt2);
		s = "$style2d$\n$header$\n set key off;set multiplot;\n";
		s += "set size 1,1;set origin 0,0;\n";
		s += "set xlabel '$xlabel$';set ylabel '$ylabel$'\n";
		s += "plot '-' with lp ls 1, '-' with lp ls 2;\n";
		s += "$data(1,2d)$\n";
		s += "set size 0.6,0.6;set origin 0.3,0.35;\n";
		s += "set xlabel 'ln($xlabel$)';set ylabel 'ln($ylabel$)';\n";
		s += "plot '-' with lp ls 1, '-' with lp ls 2;\n";
		s += "$data(2,2d)$\n";
		s += "unset multiplot;";
		jg.execute(p_opta_er_bdg, s);
	}

	public static void drawFigs(Tasks tTasks) {
		afterAll(tTasks);
		drawFigs(tTasks.sFData);
	}

	public static void main(String[] args) {
		genNetworks();
		Taskss taskss = new Taskss();
		taskss.sFLoad = "tasks#.xml";
		taskss.start();
	}
}
