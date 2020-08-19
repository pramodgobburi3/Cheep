from django.shortcuts import render
from django.http import JsonResponse
from django.contrib.auth.models import User
from django.views.decorators.csrf import csrf_exempt
from oauthlib.common import generate_token
from oauth2_provider.models import AccessToken, Application, RefreshToken
from .models import *
import datetime
from django.utils import timezone
from .serializers import UserProfileSerializer
from accounts.models import *
from plaid_api.serializers import PlaidAccessTokenSerializer

# Create your views here.

@csrf_exempt
def user_register(request):
    if request.method == "POST":
        body = request.POST
        username = body.get("username")
        firstname = body.get("firstname")
        lastname = body.get("lastname")
        password = body.get("password")
        email = body.get("email")

        new_user = User.objects.create_user(username=username, email=email, password=password,
                                            first_name=firstname, last_name=lastname)
        profile = UserProfile.objects.get(user=new_user)
        profile.firstname = firstname
        profile.lastname = lastname
        profile.email = email
        profile.save()
        tokens = manually_create_access_token(new_user)
        return JsonResponse({"status": "success", "Access_Token": tokens["Access_Token"],
                             "Refresh_Token": tokens["Refresh_Token"]})
    else:
        return JsonResponse({"status": "failed"})


@csrf_exempt
def get_user_info(request):
    if request.POST.get("access_token") is None:
        return JsonResponse({"status": "No access token provided"}, safe=False)
    else:
        try:
            access_token = AccessToken.objects.get(token=request.POST.get("access_token"),
                                                   expires__gt=timezone.now())

        except:
            try:
                access_token = AccessToken.objects.get(token=request.POST.get("access_token"))
            except:
                return JsonResponse({"status": "Invalid token"})
            else:
                return JsonResponse({"status": "Access token has expired. Request a new one."})
        else:
            user = access_token.user
            user_profile = UserProfile.objects.get(user_id=user.id)
            serialized = UserProfileSerializer(user_profile).data
            plaid_access_token = None
            if Accounts.objects.filter(user_id=user.id).exists():
                plaid_profile = Accounts.objects.get(user_id=user.id)
                plaid_access_token = plaid_profile.accessToken
            return JsonResponse({"status": "success", "user": serialized, "plaid_access_token": plaid_access_token}, safe=False)



@csrf_exempt
def example_data(request):

    if request.POST.get("access_token") is None:
        return JsonResponse({"status": "No access token provided"}, safe=False)
    else:
        try:
            access_token = AccessToken.objects.get(token=request.POST.get("access_token"),
                                                   expires__gt=timezone.now())
        except:
            try:
                access_token = AccessToken.objects.get(token=request.POST.get("access_token"))
            except:
                return JsonResponse({"status": "Invalid token"})
            else:
                return JsonResponse({"status": "Access token has expired. Request a new one."})
        else:
            return JsonResponse({"status": "success"}, safe=False)


def manually_create_access_token(user):
    apps = Application.objects.all()

    app = apps[0]
    token = generate_token()
    refresh_toekn = generate_token()
    expire_time = datetime.datetime.now() + datetime.timedelta(days=1)
    ac = AccessToken.objects.create(user=user, token=token, application=app, created=datetime.datetime.now(), expires=expire_time,
                                    scope="read write")
    RefreshToken.objects.create(user=user, token=refresh_toekn, application=app, created=datetime.datetime.now(),
                                access_token=ac)

    return {"Access_Token": token, "Refresh_Token": refresh_toekn}
