from django.db import models
from jars.models import Jars

# Create your models here.

class JarCategories(models.Model):
    jar = models.ForeignKey(Jars, related_name="jar_categories", on_delete=models.CASCADE)
    category_id = models.CharField(max_length=10, null=False, blank=False)

    class Meta:
        managed = True
        db_table = "jar_categories"
