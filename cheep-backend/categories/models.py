from django.db import models

# Create your models here.
class Category(models.Model):
    category_id = models.CharField(max_length=255)
    plaid_category_name = models.CharField(max_length=255, null=False, blank=False)
    category_name = models.CharField(max_length=255, null=False, blank=False)

    class Meta:
        managed = True
        db_table = "categories"
