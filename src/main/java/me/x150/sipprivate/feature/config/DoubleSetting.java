package me.x150.sipprivate.feature.config;

public class DoubleSetting extends SettingBase<Double> {
    final int precision;
    final double min;
    final double max;

    public DoubleSetting(Double defaultValue, String name, String description, int precision, double min, double max) {
        super(defaultValue, name, description);
        this.precision = precision;
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getPrecision() {
        return precision;
    }

    @Override
    public Double parse(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setValue(Double value) {
        if (value > max || value < min) {
            return;
        }
        super.setValue(value);
    }

    public static class Builder extends SettingBase.Builder<Builder, Double, DoubleSetting> {
        double min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY;
        int precision = 2;

        public Builder(double defaultValue) {
            super(defaultValue);
        }

        public Builder precision(int precision) {
            this.precision = precision;
            return this;
        }

        public Builder min(double min) {
            this.min = min;
            return this;
        }

        public Builder max(double max) {
            this.max = max;
            return this;
        }

        @Override
        public DoubleSetting get() {
            return new DoubleSetting(defaultValue, name, description, precision, min, max);
        }
    }
}
