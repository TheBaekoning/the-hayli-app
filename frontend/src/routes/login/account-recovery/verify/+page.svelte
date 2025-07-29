<svelte:options runes={true} />
<script lang="ts">
	import { browser } from "$app/environment";
	import { goto } from "$app/navigation";
	import Input from "$components/inputs/input.svelte";

	let hash = $state('');
	let newPassword = $state('');
	let confirmPassword = $state('');

	let sending = $state(false);
	let success = $state(false);
	let message = $state('');

	$effect(() => {
		if (!browser) return;
		hash = new URLSearchParams(window.location.search).get('encoding');
	})
</script>
<svelte:head>
	<title>Account Recovery</title>
</svelte:head>
<main>
	<h1>Set new password</h1>
	{#if !sending && !success}
		<form onsubmit={e => {
			e.preventDefault();
			if (newPassword !== confirmPassword) {
				message = 'Passwords do not match.';
				return;
			}
			sending = true;
			message = "Sending new password…";
			fetch('/login/set-password', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({
					hash,
					password: newPassword
				})
			}).then(resp => {
				if (resp.ok) {
					success = true;
					message = 'New password accepted. Redirecting you to the login page…';
					setTimeout(() => {
						goto('/login');
					}, 5000);
				} else {
					resp.json().then(json => {
						message = json.error.message;
					});
				}
			}).finally(() => {
				sending = false;
			});
		}}>
			<Input type="password" label="New Password" bind:value={newPassword} minlength="12" />
			<Input type="password" label="Confirm New Password" bind:value={confirmPassword} minlength="12" />
			<button>Set New Password</button>
		</form>
	{/if}
	<div aria-live="polite">
		{#if message}
			<p>{message}</p>
		{/if}
	</div>
</main>
