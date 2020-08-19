# Generated by Django 2.1.5 on 2019-04-02 23:33

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('jars', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='JarCategories',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('category_id', models.CharField(max_length=10)),
                ('jar', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='jars.Jars')),
            ],
            options={
                'db_table': 'jar_categories',
                'managed': True,
            },
        ),
    ]
