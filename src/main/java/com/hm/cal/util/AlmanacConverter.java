/*****************************************************************************
 * Copyright 2015 Hypotemoose, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/
package com.hm.cal.util;

import com.hm.cal.astro.Meeus;
import com.hm.cal.astro.Season;
import com.hm.cal.date.*;

/**
 * A mechanism to convert between various calendars.
 *
 * @author Chris Engelsma
 * @version 2015.10.02
 */
public class AlmanacConverter {

  /* Constants for Maya Calendar */
  private static final double _luinal = 20.0;
  private static final double _ltun = 360.0;
  private static final double _lkatun = 7200.0;
  private static final double _lbaktun = 144000.0;
  private static final double _lpiktun = 2880000.0;
  private static final double _lkalabtun = 57600000.0;
  private static final double _lkinchiltun = 1152000000.0;

/////////////////////////////////////////////////////////////////////////////
// private

  /* To Julian Day ************************************************************/
  private static final double _lalautun = 23040000000.0;

  /**
   * Converts an Almanac to a Julian day.
   *
   * @param a an Almanac.
   * @return the Julian day.
   */
  public static JulianDay toJulianDay(Almanac a) {
    if (!(a instanceof JulianDay)) {
      if (a instanceof GregorianCalendar)
        return _g2jd((GregorianCalendar) a);
      if (a instanceof JulianCalendar)
        return _jul2jd((JulianCalendar) a);
      if (a instanceof FrenchRepublicanCalendar)
        return _frc2jd((FrenchRepublicanCalendar) a);
      if (a instanceof MayaCalendar)
        return _m2jd((MayaCalendar) a);
      if (a instanceof IslamicCalendar)
        return _is2jd((IslamicCalendar) a);
      if (a instanceof HebrewCalendar)
        return _he2jd((HebrewCalendar) a);
      if (a instanceof PersianCalendar)
        return _pe2jd((PersianCalendar) a);
    }
    return (JulianDay) a;
  }

  /**
   * Converts an Almanac to a Gregorian date.
   *
   * @param a an Almanac.
   * @return the Gregorian date.
   */
  public static GregorianCalendar toGregorianCalendar(Almanac a) {
    if (a instanceof GregorianCalendar)
      return (GregorianCalendar) a;
    else
      return _jd2g(toJulianDay(a));
  }

  /**
   * Converts an Almanac to a Julian date.
   *
   * @param a an Almanac.
   * @return the Julian date.
   */
  public static JulianCalendar toJulianCalendar(Almanac a) {
    if (a instanceof JulianCalendar)
      return (JulianCalendar) a;
    else
      return _jd2jul(toJulianDay(a));
  }

  /**
   * Converts an Almanac to a French Republican date.
   *
   * @param a an Almanac.
   * @return the French Republican date.
   */
  public static FrenchRepublicanCalendar toFrenchRepublicanCalendar(Almanac a) {
    if (a instanceof FrenchRepublicanCalendar)
      return (FrenchRepublicanCalendar) a;
    else
      return _jd2frc(toJulianDay(a));
  }

  /**
   * Converts an Almanac to a Maya date.
   *
   * @param a an Almanac.
   * @return the Maya date.
   */
  public static MayaCalendar toMayaCalendar(Almanac a) {
    if (a instanceof MayaCalendar)
      return (MayaCalendar) a;
    else
      return _jd2m(toJulianDay(a));
  }

  /**
   * Converts an Almanac to an Islamic date.
   *
   * @param a an Almanac.
   * @return the Islamic date.
   */
  public static IslamicCalendar toIslamicCalendar(Almanac a) {
    if (a instanceof IslamicCalendar)
      return (IslamicCalendar) a;
    else
      return _jd2is(toJulianDay(a));
  }

  /**
   * Converts an Almanac to a Hebrew date.
   *
   * @param a an Almanac
   * @return the Hebrew date.
   */
  public static HebrewCalendar toHebrewCalendar(Almanac a) {
    if (a instanceof HebrewCalendar)
      return (HebrewCalendar) a;
    else
      return _jd2he(toJulianDay(a));
  }

  public static PersianCalendar toPersianCalendar(Almanac a) {
    if (a instanceof PersianCalendar)
      return (PersianCalendar) a;
    else
      return _jd2pe(toJulianDay(a));
  }

  private static JulianDay _g2jd(GregorianCalendar date) {
    int month = date.getMonth();
    int year = date.getYear();
    int day = date.getDay();
    if (month == 1 || month == 2) {
      year--;
      month += 12;
    }
    int a = (int) Math.floor(year / 100);
    int b = a / 4;
    int c = 2 - a + b;
    int e = (int) (365.25 * (year + 4716));
    int f = (int) (30.6001 * (month + 1));
    double jd = c + day + e + f - 1524.5;
    return new JulianDay(jd);
  }

  private static JulianDay _jul2jd(JulianCalendar date) {
    int month = date.getMonth();
    int year = date.getYear();
    int day = date.getDay();

    if (year < 1) year++;
    if (month <= 2) {
      year--;
      month += 12;
    }

    double jd = (Math.floor(365.25 * (year + 4716)) +
      Math.floor(30.6001 * (month + 1)) +
      day) - 1524.5;

    return new JulianDay(jd);
  }

  private static JulianDay _frc2jd(FrenchRepublicanCalendar date) {
    int year = date.getYear();
    int month = date.getMonth();
    int week = date.getWeek();
    int day = date.getDay(false);

    double guess = date.EPOCH.getValue() + (Meeus.TROPICAL_YEAR * ((year - 1) - 1));
    double[] adr = new double[2];
    adr[0] = year - 1;
    adr[1] = 0;

    while (adr[0] < year) {
      adr = anneeDeLaRevolution(new JulianDay(guess));
      guess = adr[1] + (Meeus.TROPICAL_YEAR + 2);
    }
    double equinox = adr[1];

    double jd = equinox +
      (30 * (month - 1)) +
      (10 * (week - 1)) +
      (day - 1);

    return new JulianDay(jd);
  }

  private static JulianDay _m2jd(MayaCalendar date) {
    int baktun = date.getBaktun();
    int katun = date.getKatun();
    int tun = date.getTun();
    int uinal = date.getUinal();
    int kin = date.getKin();

    double jd = date.EPOCH.getValue() +
      (baktun * 144000) +
      (katun * 7200) +
      (tun * 360) +
      (uinal * 20) +
      kin;
    return new JulianDay(jd);
  }

  private static JulianDay _is2jd(IslamicCalendar date) {
    int day = date.getDay();
    int month = date.getMonth();
    int year = date.getYear();
    int astro = date.getCalendarType().getValue();
    double jd = (day +
      Math.ceil(29.5 * (month - 1)) +
      (year - 1) * 354 +
      Math.floor((3 + (11 * year)) / 30) +
      (IslamicCalendar.EPOCH.getValue()-astro)) - 1;
    return new JulianDay(jd);
  }

  private static JulianDay _he2jd(HebrewCalendar date) {
    int day = date.getDay();
    int month = date.getMonth();
    int year = date.getYear();
    int months = date.getNumberOfMonthsInYear();
    double jd = HebrewCalendar.EPOCH.getValue() +
      delayHebrewYear(year) +
      delayHebrewYearAdjacent(year) +
      day + 1;

    if (month < 7) {
      for (int i = 7; i <= months; ++i) {
        jd += HebrewCalendar.getNumberOfDaysInMonth(year, i);
      }

      for (int i = 1; i < month; ++i) {
        jd += HebrewCalendar.getNumberOfDaysInMonth(year, i);
      }
    } else {
      for (int i = 7; i < month; ++i) {
        jd += HebrewCalendar.getNumberOfDaysInMonth(year, i);
      }
    }

    return new JulianDay(jd);
  }

  private static JulianDay _pe2jd(PersianCalendar date) {
    int year = date.getYear();
    int month = date.getMonth();
    int day = date.getDay();
    double epoch = PersianCalendar.EPOCH.getValue();
    double guess = (epoch - 1) + (Meeus.TROPICAL_YEAR * ((year - 1) - 1));
    double[] adr = new double[]{year - 1, 0};
    while (adr[0] < year) {
      adr = persianAstronomicalYear(guess);
      guess = adr[1] + Meeus.TROPICAL_YEAR + 2;
    }
    double equinox = adr[1];
    double jd = equinox + (day - 1) +
      ((month <= 7) ? ((month - 1) * 31) : (((month - 1) * 30) + 6));
    jd += 0.5;
    return new JulianDay(jd);
  }

  private static GregorianCalendar _jd2g(JulianDay jd) {
    int J = (int) (jd.getValue() + 0.5);
    int y = 4716, j = 1401, m = 2;
    int n = 12, r = 4, p = 1461;
    int v = 3, u = 5, s = 153;
    int w = 2, B = 274277, C = -38;
    int f = J + j + (((4 * J + B) / 146097) * 3) / 4 + C;
    int e = r * f + v;
    int g = (e % p) / r;
    int h = u * g + w;
    int day = (h % s) / u + 1;
    int month = (h / s + m) % n + 1;
    int year = (e / p) - y + (n + m - month) / n;
    return new GregorianCalendar(year, month, day);
  }

  private static JulianCalendar _jd2jul(JulianDay jd) {
    double jday = jd.getValue();
    double a = Math.floor(jday + 0.5f);
    double b = a + 1524;
    double c = Math.floor((b - 122.10) / 365.25);
    double d = Math.floor(365.25 * c);
    double e = Math.floor((b - d) / 30.6001);

    int month = (int) Math.floor((e < 14) ? (e - 1) : (e - 13));
    int year = (int) Math.floor((month > 2) ? (c - 4716) : (c - 4715));
    int day = (int) (b - d - Math.floor(30.6001 * e));

    // Since there's no "0" year.
    if (year < 1)
      year--;

    return new JulianCalendar(year, month, day);
  }

  private static FrenchRepublicanCalendar _jd2frc(JulianDay jd) {
    int year, month, week, day;
    double dd = jd.getValue();
    double jday = Math.floor(dd) + 0.5;
    double[] adr = anneeDeLaRevolution(new JulianDay(jd));
    year = (int) adr[0];
    double equinoxe = adr[1];
    month = (int) (Math.floor((dd - equinoxe) / 30) + 1);
    double djour = (dd - equinoxe) % 30;
    week = (int) (Math.floor(djour / 10) + 1);
    day = (int) ((djour % 10) + 1);

    if (day > 10) {
      day -= 12;
      week = 1;
      month = 13;
    }
    if (month == 13) {
      week = 1;
      if (day > 6) {
        day = 1;
      }
    }

    return new FrenchRepublicanCalendar(year, month, week, day);
  }

  private static MayaCalendar _jd2m(JulianDay jd) {
    int baktun, katun, tun, uinal, kin;
    double day = jd.atMidnight().getValue();
    double d = day - MayaCalendar.EPOCH.getValue();
    baktun = (int) Math.floor(d / _lbaktun);
    d = d % _lbaktun;
    katun = (int) Math.floor(d / _lkatun);
    d = d % _lkatun;
    tun = (int) Math.floor(d / _ltun);
    d = d % _ltun;
    uinal = (int) Math.floor(d / _luinal);
    kin = (int) (d % _luinal);
    return new MayaCalendar(baktun, katun, tun, uinal, kin);
  }

  private static IslamicCalendar _jd2is(JulianDay jd) {
    double epoch = IslamicCalendar.EPOCH.getValue();
    double jday = jd.atMidnight().getValue();
    int year = (int) Math.floor(((30 * (jday - epoch)) + 10646) / 10631);

    double guessy = _is2jd(new IslamicCalendar(year, 1, 1)).getValue();
    int month = (int) Math.min(12, Math.ceil((jday - (29 + guessy)) / 29.5) + 1);

    double guessm = _is2jd(new IslamicCalendar(year, month, 1)).getValue();
    int day = (int) (jday - guessm) + 1;

    return new IslamicCalendar(year, month, day);
  }

  private static HebrewCalendar _jd2he(JulianDay jd) {
    int year, month, day;
    double epoch = HebrewCalendar.EPOCH.getValue();
    double jday = jd.atMidnight().getValue();
    int count = (int) Math.floor(((jday - epoch) * 98496.0) / 35975351.0);

    year = count - 1;

    HebrewCalendar cal = new HebrewCalendar(count, 7, 1);
    double guess = _he2jd(cal).getValue();
    for (int i = count; jday >= guess; ++i) {
      year++;
      cal.setYear(i + 1);
      guess = _he2jd(cal).getValue();
    }

    int first = (jday < _he2jd(new HebrewCalendar(year, 1, 1)).getValue()) ? 7 : 1;
    month = first;

    cal.set(year, first, HebrewCalendar.getNumberOfDaysInMonth(year, first));

    for (int i = first; jday > _he2jd(new HebrewCalendar(year, i, HebrewCalendar.getNumberOfDaysInMonth(year, i))).getValue(); ++i) {
      ++month;
    }
    cal.set(year, month, 1);
    day = (int) (jday - _he2jd(cal).getValue()) + 1;
    return new HebrewCalendar(year, month, day);
  }

  private static PersianCalendar _jd2pe(JulianDay jd) {
    double jday = jd.atMidnight().getValue();
    double[] adr = persianAstronomicalYear(jday);
    int year = (int) adr[0];
    double yearDay = Math.floor(jday) - Math.floor(_pe2jd(new PersianCalendar(year, 1, 1)).getValue()) + 1;
    int month = (yearDay <= 186) ? (int) Math.ceil(yearDay / 31.0) : (int) (Math.ceil((yearDay - 6) / 30.0));
    int day = (int) (Math.floor(jday) - Math.floor(_pe2jd(new PersianCalendar(year, month, 1)).getValue())) + 1;
    return new PersianCalendar(year, month, day);
  }


  /**
   * Delay start of new year so it doesn't fall on Sunday, Wednesday or
   * Friday.
   *
   * @param year a year.
   * @return the delay in days.
   */
  protected static int delayHebrewYear(int year) {
    int months = (int) Math.floor(((235 * year) - 234) / 19);
    int parts = 12084 + (13753 * months);
    int day = (months * 29) + (int) Math.floor(parts / 25920);
    if ((3 * (day + 1)) % 7 < 3)
      ++day;
    return day;
  }

  /**
   * Check for delay due to length of adjacent years.
   *
   * @param year a year.
   * @return the delay in days.
   */
  protected static int delayHebrewYearAdjacent(int year) {
    int last = delayHebrewYear(year - 1);
    int now = delayHebrewYear(year);
    int next = delayHebrewYear(year + 1);
    int val = ((next - now) == 356) ? 2 : (((now - last) == 382) ? 1 : 0);
    return val;
  }

  private static double[] persianAstronomicalYear(double jday) {
    int guess = toGregorianCalendar(new JulianDay(jday)).getYear() - 2;
    double lastEquinox = _tehranEquinox(guess);
    while (lastEquinox > jday) {
      guess--;
      lastEquinox = _tehranEquinox(guess);
    }
    double nextEquinox = lastEquinox - 1;
    while (!((lastEquinox <= jday) && (jday < nextEquinox))) {
      lastEquinox = nextEquinox;
      guess++;
      nextEquinox = _tehranEquinox(guess);
    }
    double adr = (lastEquinox - PersianCalendar.EPOCH.getValue());
    adr /= Meeus.TROPICAL_YEAR;
    adr += 1;
    return new double[]{Math.round(adr), lastEquinox};
  }

  private static double[] anneeDeLaRevolution(JulianDay julday) {
    int guess = (new GregorianCalendar(julday)).getYear() - 2;
    double nexteq, lasteq, jd;
    jd = julday.getValue();
    lasteq = _parisEquinox(guess);
    while (lasteq > jd) {
      guess--;
      lasteq = _parisEquinox(guess);
    }
    nexteq = lasteq - 1;
    while (!((lasteq <= jd) && (jd < nexteq))) {
      lasteq = nexteq;
      guess++;
      nexteq = _parisEquinox(guess);
    }
    double adr = (lasteq - FrenchRepublicanCalendar.EPOCH.getValue());
    adr /= Meeus.TROPICAL_YEAR;
    adr += 1;
    return new double[]{Math.round(adr), lasteq};
  }

  private static double _parisEquinox(int year) {
    double eqJED = Meeus.equinox(year, Season.AUTUMN);
    double eqJD = eqJED - Meeus.deltat(year) / (24.0 * 60.0 * 60.0);
    double eqAPP = eqJD + Meeus.equationOfTime(eqJED);
    double dtParis = (2.0 + (20.0 / 60.0) + (15.0 / (60.0 * 60.0))) / 360.0;
    double eqParis = eqAPP + dtParis;
    eqParis = Math.floor(eqParis - 0.5) + 0.5;
    return eqParis;
  }

  private static double _tehranEquinox(int year) {
    double eqJED = Meeus.equinox(year, Season.SPRING);
    double eqJD = eqJED - Meeus.deltat(year) / (24.0 * 60.0 * 60.0);
    double eqApp = eqJD + Meeus.equationOfTime(eqJED);
    double dtTehran = (52.0 + (30.0 / 60.0)) / 360.0;
    double eqTehran = eqApp + dtTehran;
    eqTehran = Math.floor(eqTehran);
    return eqTehran;
  }

}
