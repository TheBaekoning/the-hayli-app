<svelte:options runes={true} />
<script lang="ts">
	import Input from "$components/inputs/input.svelte";
	import { apiFetch } from "$lib/api-fetch";

	let email: string = $state('');
	let sending: boolean = $state(false);
	let success: boolean = $state(false);
	let ariaLiveContainer: HTMLDivElement = $state(null);
	let form: HTMLFormElement = $state(null);
	let message: string = $state('');
</script>
<svelte:head>
	<title>Account Recovery</title>
</svelte:head>
<main>
	<h1>Account Recovery</h1>
	{#if !sending && !success}
		<p>Enter your email address below and we'll send you a link to reset your password.</p>
		<form bind:this={form} onsubmit={e => {
			e.preventDefault();
			apiFetch({
				url: '/login/send/forgot',
				options: {
					method: 'POST',
					body: JSON.stringify({ email })
				}
			}).then(resp => {
				success = resp.ok;
				if (success) {
					email = '';
					message = 'If there is an account associated with that email address, we will send a recovery email. It may take a few minutes to arrive. Please be sure to check your spam folder.';
				} else {
					resp.json().then(json => {
						console.error(json);
					});
					form.focus();
				}
			}).finally(() => {
				sending = false;
			});
			sending = true;
			ariaLiveContainer.focus();
		}}>
			<Input type="email" label="Email" bind:value={email} />
			<button>Send Recovery Email</button>
		</form>
	{/if}
	<div bind:this={ariaLiveContainer} aria-live="polite" tabindex="-1">
		{#if sending}
			<p>Sending recovery email...</p>
		{/if}
		{#if success}
			<p>Recovery email sent.</p>
		{/if}
		{#if message}
			<p>{message}</p>
		{/if}
	</div>
</main>