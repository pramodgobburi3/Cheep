# Generated by Django 2.1.5 on 2019-03-31 01:30

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('budgets', '0002_auto_20190331_0127'),
    ]

    operations = [
        migrations.AlterField(
            model_name='budgets',
            name='last_transaction',
            field=models.CharField(blank=True, max_length=250, null=True),
        ),
    ]