package com.playray.connection;

import com.playray.connection.Class136;

class Class133 {

    private int anInt2034;
    private int anInt2035;
    private int[][] anIntArrayArray2036;
    private int[][] anIntArrayArray2037;


    protected Class133(int var1) {
        this.anInt2035 = var1;
        this.anInt2034 = -1;
        this.anIntArrayArray2036 = new int[2][125];
        this.anIntArrayArray2037 = new int[2][1920];
    }

    protected static int method2094() {
        int var0 = 100000000;
        int var1 = 999999999;
        int var2 = var0 + (int) (Math.random() * (double) (var1 - var0 + 1));
        return var2;
    }

    protected void method2095(int var1) {
        this.anInt2034 = var1;

        int var2;
        for (var2 = 1; var2 <= 125; ++var2) {
            this.anIntArrayArray2036[1][var2 - 1] = -1;
        }

        for (var2 = 128; var2 <= 2047; ++var2) {
            this.anIntArrayArray2037[1][var2 - 128] = -1;
        }

        Class136 var5 = new Class136((long) var1);

        int var3;
        int var4;
        for (var4 = 1; var4 <= 125; this.anIntArrayArray2036[1][var3 - 1] = var4++) {
            do {
                var3 = var5.method2130(1, 125);
            } while (this.anIntArrayArray2036[1][var3 - 1] >= 0);

            this.anIntArrayArray2036[0][var4 - 1] = var3;
        }

        for (var4 = 128; var4 <= 2047; this.anIntArrayArray2037[1][var3 - 128] = var4++) {
            do {
                var3 = var5.method2130(128, 2047);
            } while (this.anIntArrayArray2037[1][var3 - 128] >= 0);

            this.anIntArrayArray2037[0][var4 - 128] = var3;
        }

    }

    protected void method2096() {
        this.anInt2034 = -1;
    }

    protected String method2097(String var1) {
        if (this.anInt2034 == -1) {
            return var1;
        } else {
            char[] var2 = var1.toCharArray();
            int var3 = var2.length;
            StringBuffer var4 = new StringBuffer(var3 + 2);
            int var5 = (int) (1.0D + Math.random() * 125.0D);
            int var6 = (int) (1.0D + Math.random() * 125.0D);
            int var7 = method2102(var5, 1, var3 + 1);
            int var8 = this.method2100(var6);
            var4.append((char) this.method2100(var5));
            int var10 = this.anInt2034 % 99 - 49 + var5 - var6;

            for (int var11 = 0; var11 < var3; ++var11) {
                if (var7 == var11 + 1) {
                    var4.append((char) var8);
                }

                int var9 = var2[var11];
                if (var9 >= 1 && var9 <= 127) {
                    if (var9 != 10 && var9 != 13) {
                        var9 = this.method2099(var9);
                        var9 = method2101(var9, var10, 1, 125);
                        ++var10;
                        var9 = this.anIntArrayArray2036[0][var9 - 1];
                        var9 = this.method2100(var9);
                        if (var9 >= 14 && var9 <= 127) {
                            var9 = method2101(var9, this.anInt2035 - 9, 14, 127);
                        }
                    }
                } else if (var9 >= 128 && var9 <= 2047) {
                    var9 = method2101(var9, var10, 128, 2047);
                    var10 += 2;
                    var9 = this.anIntArrayArray2037[0][var9 - 128];
                }

                var4.append((char) var9);
                ++var10;
            }

            if (var7 == var3 + 1) {
                var4.append((char) var8);
            }

            return var4.toString();
        }
    }

    protected String decrypt(String var1) {
        if (this.anInt2034 == -1) {
            return var1;
        } else {
            char[] var2 = var1.toCharArray();
            int var3 = var2.length;
            StringBuffer var4 = new StringBuffer(var3 - 2);
            int var5 = this.method2099(var2[0]);
            int var6 = method2102(var5, 1, var3 - 1);
            int var7 = this.method2099(var2[var6]);
            int var9 = var7 - var5 - (this.anInt2034 % 99 - 49);
            int var10 = var6 < var3 - 1 ? var3 : var3 - 1;

            for (int var11 = 1; var11 < var10; ++var11) {
                if (var11 == var6) {
                    ++var11;
                }

                int var8 = var2[var11];
                if (var8 >= 1 && var8 <= 127) {
                    if (var8 != 10 && var8 != 13) {
                        if (var8 >= 14 && var8 <= 127) {
                            var8 = method2101(var8, 9 - this.anInt2035, 14, 127);
                        }

                        var8 = this.method2099(var8);
                        var8 = this.anIntArrayArray2036[1][var8 - 1];
                        var8 = method2101(var8, var9, 1, 125);
                        --var9;
                        var8 = this.method2100(var8);
                    }
                } else if (var8 >= 128 && var8 <= 2047) {
                    var8 = this.anIntArrayArray2037[1][var8 - 128];
                    var8 = method2101(var8, var9, 128, 2047);
                    var9 -= 2;
                }

                var4.append((char) var8);
                --var9;
            }

            return var4.toString();
        }
    }

    private int method2099(int var1) {
        if (var1 > 13) {
            --var1;
        }

        if (var1 > 10) {
            --var1;
        }

        return var1;
    }

    private int method2100(int var1) {
        if (var1 >= 10) {
            ++var1;
        }

        if (var1 >= 13) {
            ++var1;
        }

        return var1;
    }

    private static int method2101(int var0, int var1, int var2, int var3) {
        return method2102(var0 + var1, var2, var3);
    }

    private static int method2102(int var0, int var1, int var2) {
        int var3 = var1;
        boolean var6 = false;
        var2 -= var3;
        var0 -= var3;
        int var4 = var2 + 1;
        if (var0 > var2) {
            var0 %= var4;
        } else if (var0 < 0) {
            int var5 = (-var0 - 1) / var4 + 1;
            var0 += var5 * var4;
        }

        var0 += var3;
        return var0;
    }
}
